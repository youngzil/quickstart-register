#!/bin/bash

#TOMCAT_HOME=/Users/lengfeng/software/apache-tomcat-10.0.2
# 10.X版本有问题
TOMCAT_HOME=/Users/lengfeng/software/apache-tomcat-9.0.43
BASE_DIR=$(dirname $0)

mvn -DskipTests clean install -U

cp $BASE_DIR/target/zkWeb-1.0.war $TOMCAT_HOME/webapps

sh $TOMCAT_HOME/bin/catalina.sh run

#sh $TOMCAT_HOME/bin/startup.sh

#sh $TOMCAT_HOME/bin/shutdown.shclear

#访问
#http://localhost:8080/zkWeb-1.0/
#http://localhost:8081/zkWeb-1.0/

