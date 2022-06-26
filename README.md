# KCBApp

## Build: ##
      
>Clone the project.\
$ mvn clean install\
$ cd target\
$ zip -r KCBApp.zip KCBApp-0.0.1-SNAPSHOT.jar config


## Run: ##

>In DMZ server:
1. Switch to root.

Create the following file:
$ vi /etc/systemd/system/kfs.service

[Unit]
Description=KFS KCB Integration
After=syslog.target

[Service]
User=root
WorkingDirectory=/home/kfs
ExecStart=/usr/bin/java -Dserver.port=8081 -jar /home/kfs/KCBApp-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=30

[Install]
WantedBy=multi-user.target

$ ls -lrt kfs.service
kfs.service -> /usr/lib/systemd/system/kfs.service

Enable the service:
$ systemctl enable kfs.service

Start the service:
systemctl start kfs

To Check the service status:
systemctl status kfs

To Stop the service:
systemctl stop kfs

## Test: ##
    
>In UAT:
curl -vvv http://localhost:8081/query -H "Content-Type: application/json" -d @query.json
curl -vvv http://192.168.17.6:8081/tenderdeposit -H "Content-Type: application/json" -d @tender.json
