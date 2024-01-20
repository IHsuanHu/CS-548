# Assignment 1: Cloud Computing

In this assignment, you will set up a platform as a service (PaaS) on top of an infrastructure as a service (IaaS). You will deploy appications on this PaaS in some of the later assignments.

### Steps for this assignment
1. Create an EC2 instance on AWS
2. Use SSH to access EC2 instance, here is command line: ```ssh -i XXXXXX.pem ec2-user@``` + public Ipv4 address
3. Apply all updates; ```sudo yum update```
4. Then run the command below:
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
### Upload Web application
1. Use this command line: ```scp -i XXXX.pem chat-webapp.war ec2-user@ec2_elastic_ip:/home/ec2-user .```
