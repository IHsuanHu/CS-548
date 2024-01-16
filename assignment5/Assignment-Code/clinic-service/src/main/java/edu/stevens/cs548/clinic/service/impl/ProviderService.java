package edu.stevens.cs548.clinic.service.impl;

import edu.stevens.cs548.clinic.domain.*;
import edu.stevens.cs548.clinic.domain.IPatientDao.PatientExn;
import edu.stevens.cs548.clinic.domain.IProviderDao.ProviderExn;
import edu.stevens.cs548.clinic.domain.ITreatmentDao.TreatmentExn;
import edu.stevens.cs548.clinic.service.IPatientService.PatientNotFoundExn;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * CDI Bean implementation class ProviderService
 */
// TODO
    @RequestScoped
    @Transactional
public class ProviderService implements IProviderService {

    @SuppressWarnings("unused")
    private Logger logger = Logger.getLogger(ProviderService.class.getCanonicalName());

    private IProviderFactory providerFactory;

    private ProviderDtoFactory providerDtoFactory;


    public ProviderService() {
        // Initialize factories
        providerFactory = new ProviderFactory();
        providerDtoFactory = new ProviderDtoFactory();
    }

    // TODO
    @Inject
    private IProviderDao providerDao;

    // TODO
    @Inject
    private IPatientDao patientDao;


    /**
     * @see IProviderService#addProvider(ProviderDto dto)
     */
    @Override
    public UUID addProvider(ProviderDto dto) throws ProviderServiceExn {
        // Use factory to create Provider entity, and persist with DAO
        try {
            Provider provider = providerFactory.createProvider();
            if (dto.getId() == null) {
                provider.setProviderId(UUID.randomUUID());
            } else {
                provider.setProviderId(dto.getId());
            }
            provider.setName(dto.getName());
            provider.setNpi(dto.getNpi());
            providerDao.addProvider(provider);
            return provider.getProviderId();
        } catch (ProviderExn e) {
            throw new ProviderServiceExn("Failed to add provider", e);
        }
    }

    public List<ProviderDto> getProviders() throws ProviderServiceExn {
        try {
            List<ProviderDto> dtos = new ArrayList<ProviderDto>();
            Collection<Provider> providers = providerDao.getProviders();
            for (Provider provider : providers) {
                dtos.add(providerToDto(provider, false));
            }
            return dtos;
        } catch (TreatmentExn e) {
            throw new ProviderServiceExn("Failed to export treatment", e);
        }
    }

    /**
     * @see IProviderService#getProvider(UUID)
     */
    @Override
    /*
     * The boolean flag indicates if related treatments should be loaded eagerly.
     */
    public ProviderDto getProvider(UUID id, boolean includeTreatments) throws ProviderServiceExn {
        // TODO use DAO to get Provider by external key
        try {
            Provider provider = providerDao.getProvider(id, includeTreatments);
            return providerToDto(provider, includeTreatments);
        } catch (ProviderExn e) {
            throw new ProviderServiceExn("Failed to get provider", e);
        } catch (TreatmentExn e) {
            throw new ProviderServiceExn("Failed to export treatments", e);
        }

    }

    @Override
    /*
     * By default, we eagerly load related treatments with a provider record.
     */
    public ProviderDto getProvider(UUID id) throws ProviderServiceExn {
        return getProvider(id, true);
    }

    private ProviderDto providerToDto(Provider provider, boolean includeTreatments) throws TreatmentExn {
        ProviderDto dto = providerDtoFactory.createProviderDto();
        dto.setId(provider.getProviderId());
        dto.setName(provider.getName());
        dto.setNpi(provider.getNpi());
        if (includeTreatments) {
            dto.setTreatments(provider.exportTreatments(TreatmentExporter.exportWithoutFollowups()));
        }
        return dto;
    }

    private void addTreatmentImpl(TreatmentDto dto, Consumer<Treatment> parentFollowUps)
            throws PatientServiceExn, ProviderServiceExn {
        try {
            /*
             * Set the external key here.
             */
            if (dto.getId() == null) {
                dto.setId(UUID.randomUUID());
            }

            Provider provider = providerDao.getProvider(dto.getProviderId());
            Patient patient = patientDao.getPatient(dto.getPatientId());

            Consumer<Treatment> followUpsConsumer;

            if (dto instanceof DrugTreatmentDto) {

                DrugTreatmentDto drugTreatmentDto = (DrugTreatmentDto) dto;
                followUpsConsumer = provider.importtDrugTreatment(dto.getId(), patient, provider, dto.getDiagnosis(),
                        drugTreatmentDto.getDrug(), drugTreatmentDto.getDosage(), drugTreatmentDto.getStartDate(),
                        drugTreatmentDto.getEndDate(), drugTreatmentDto.getFrequency(), parentFollowUps);

                /*
                 * TODO Handle the other cases
                 */

            } else if (dto instanceof RadiologyTreatmentDto) {
                RadiologyTreatmentDto radiologyTreatmentDto = (RadiologyTreatmentDto) dto;
                followUpsConsumer = provider.importRadiology(dto.getId(), patient, provider, dto.getDiagnosis(),
                        radiologyTreatmentDto.getTreatmentDates(),parentFollowUps);
            } else if (dto instanceof  PhysiotherapyTreatmentDto) {
                PhysiotherapyTreatmentDto physiotherapyTreatmentDto = (PhysiotherapyTreatmentDto) dto;
                followUpsConsumer = provider.importPhysiotherapy(dto.getId(), patient, provider, dto.getDiagnosis(),
                        physiotherapyTreatmentDto.getTreatmentDates(), parentFollowUps);
            } else if (dto instanceof SurgeryTreatmentDto) {
                SurgeryTreatmentDto surgeryTreatmentDto = (SurgeryTreatmentDto) dto;
                followUpsConsumer = provider.importSurgery(dto.getId(), patient, provider, dto.getDiagnosis(),
                        surgeryTreatmentDto.getSurgeryDate() ,surgeryTreatmentDto.getDischargeInstructions(), parentFollowUps);
            } else {

                throw new IllegalArgumentException("No treatment-specific info provided.");

            }

            for (TreatmentDto followUp : dto.getFollowupTreatments()) {
                addTreatmentImpl(followUp, followUpsConsumer);
            }

        } catch (PatientExn e) {
            throw new PatientNotFoundExn("Could not find patient for " + dto.getPatientId(), e);

        } catch (ProviderExn e) {
            throw new ProviderNotFoundExn("Could not find provider for " + dto.getProviderId(), e);
        }
    }

    @Override
    public void addTreatment(TreatmentDto dto) throws PatientServiceExn, ProviderServiceExn {
        addTreatmentImpl(dto, null);
    }

    @Override
    public TreatmentDto getTreatment(UUID providerId, UUID tid)
            throws ProviderNotFoundExn, TreatmentNotFoundExn, ProviderServiceExn {
        // Export treatment DTO from Provider aggregate
        try {
            Provider provider = providerDao.getProvider(providerId);
            TreatmentDto treatment = provider.exportTreatment(tid, TreatmentExporter.exportWithoutFollowups());
            return treatment;

        } catch (TreatmentExn e) {
            throw new TreatmentNotFoundExn("Could not find treatment for " + tid, e);

        } catch (ProviderExn e) {
            throw new ProviderNotFoundExn("Could not find provider for " + providerId, e);
        }
    }


    @Override
    public void removeAll() throws ProviderServiceExn {
        providerDao.deleteProviders();
    }

}
