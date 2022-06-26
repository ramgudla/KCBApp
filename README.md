# KCBApp

## Build: ##
      
>Clone the project.\
$ mvn clean install\
$ cd target\
$ zip -r KCBApp.zip KCBApp-0.0.1-SNAPSHOT.jar config


## Run: ##

>In PROD server:
1. Switch to root.
2. $ mkdir -p /home/kfs
3. $ unzip KCBApp.zip 
4. Create the following file:\
      $ vi /etc/systemd/system/kfs.service

      [Unit]
      Description=KFS KCB Integration
      After=syslog.target

      [Service]
      User=root\
      WorkingDirectory=/home/kfs\
      ExecStart=/usr/bin/java -Dserver.port=8081 -jar /home/kfs/KCBApp-0.0.1-SNAPSHOT.jar --logging.config=/home/kfs/config/logback-spring.xml --spring.config.location=/home/kfs/config/application.properties \
      SuccessExitStatus=143\
      Restart=always\
      RestartSec=30

      [Install]
      WantedBy=multi-user.target


5. Enable the service:\
      $ systemctl daemon-reload\
      $ systemctl enable kfs.service\
      $ ls -lrt kfs.service\
      kfs.service -> /usr/lib/systemd/system/kfs.service\
      
      If the above gives errors, try:\
      $ mv /etc/systemd/system/kfs.service /usr/lib/systemd/system/\
      $ ln -s /usr/lib/systemd/system/kfs.service /etc/systemd/system/kfs.service\
      $ systemctl enable kfs.service

6. Start the service:\
      $ systemctl start kfs

7. To Check the service status:\
      $ systemctl status kfs

8. To Stop the service:\
      $ systemctl stop kfs

## Test: ##

      $ curl -vvv http://localhost:8081/query -H "Content-Type: application/json" -d @query.json\
      $ curl -vvv http://192.168.17.6:8081/tenderdeposit -H "Content-Type: application/json" -d @tender.json
