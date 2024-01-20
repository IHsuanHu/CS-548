# Assignment 1: Cloud Computing

In this assignment, you will set up a platform as a service (PaaS) on top of an infrastructure as a service (IaaS). You will deploy appications on this PaaS in some of the later assignments.

### Steps for this assignment
1. Create an EC2 instance on AWS
2. Use SSH to access EC2 instance, here is command line: ```ssh -i XXXXXX.pem ec2-user@``` + public Ipv4 address
3. Apply all updates; ```sudo yum update```
4. Then run the command below to mount the disk for the database
```EC2
[ec2-user@ip-172-31-31-147 ~]$ sudo su -
[root@ip-172-31-31-147 ~]# fdisk -l
[root@ip-172-31-31-147 ~]# mkfs -t ext3 /dev/xvdb
[root@ip-172-31-31-147 ~]# file -s /dev/xvdb
[root@ip-172-31-31-147 ~]# echo "UUID=fea17bfd-8c02-4d20-9e3f-e90a504aba4c /data ext3 noatime 0 0" >> /etc/fstab
[root@ip-172-31-31-147 ~]# mkdir /data
[root@ip-172-31-31-147 ~]# mount /data
[root@ip-172-31-31-147 ~]# fdisk -l
[root@ip-172-31-31-147 ~]# exit
```
5. Install Docker on the instance
```EC@
[ec2-user@ip-172-31-31-147 ~]$ sudo yum install -y docker
[ec2-user@ip-172-31-31-147 ~]$ sudo service docker start
```
6. Create database container
```EC2
[ec2-user@ip-172-31-31-147 ~]$ docker network create --driver bridge cs548-network
[ec2-user@ip-172-31-31-147 ~]$ sudo docker network create --driver bridge cs548-network
[ec2-user@ip-172-31-31-147 ~]$ sudo docker pull postgres
[ec2-user@ip-172-31-31-147 ~]$ sudo docker run -d --name cs548db --network cs548-network -p 5432:5432 -v /data:/var/lib/postgresql/data -e POSTGRES_PASSWORD=0000 -e PGDATA=/var/lib/postgresql/data/pgdata postgres
[ec2-user@ip-172-31-31-147 ~]$ sudo docker ps
```
7. Initialize database user and create database
```EC2
[ec2-user@ip-172-31-31-147 ~]$ sudo docker run -it --rm --network cs548-network postgres /bin/bash
root@78fc94b073b6:/# createuser cs548user -P --createdb -h cs548db -U postgres
Enter password for new role:
Enter it again:
Password:
root@78fc94b073b6:/# exit

[ec2-user@ip-172-31-31-147 ~]$ sudo docker run -it --rm --network cs548-network postgres psql -h cs548db -U postgres
Password for user postgres:
postgres=# create database cs548 with owner cs548user;
CREATE DATABASE
postgres=# \q
```

### Upload Web application
Use this command line: ```scp -i XXXX.pem chat-webapp.war ec2-user@ec2_elastic_ip:/home/ec2-user .```

8. Create application container
```EC2
[ec2-user@ip-172-31-31-147 ~]$ mkdir cs548-payara
[ec2-user@ip-172-31-31-147 ~]$ mv chat-webapp.war cs548-payara/
[ec2-user@ip-172-31-31-147 ~]$ cd cs548-payara/
[ec2-user@ip-172-31-31-147 cs548-payara]$ vi Dockerfile

FROM payara/micro:6.2023.6-jdk17
COPY --chown=payara:payara chat-webapp.war ${DEPLOY_DIR}
CMD [ "--contextroot", "chat", "--deploy", "/opt/payara/deployments/chat-webapp.war" ]

[ec2-user@ip-172-31-31-147 cs548-payara]$ sudo docker build -t cs548/chat .
[ec2-user@ip-172-31-31-147 ~]$ sudo docker run -d --name chat --network cs548-network -p 8080:8080 -p 8181:8181 -e DATABASE_USERNAME=cs548user -e DATABASE_PASSWORD=0000 -e DATABASE=cs548 -e DATABASE_HOST=cs548db cs548/chat
[ec2-user@ip-172-31-31-147 ~]$ sudo docker ps
```
9. Access to the web application
```http://ec2_ip:8080/chat```
