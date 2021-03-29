# zkWeb
在阿里zkWeb基础上升级</br>
数据存储弃用H2，改用derby，不需要外部安装启动数据库


原项目地址
https://github.com/shiranjia/zkWeb


zookeeper控制台zkWeb
https://github.com/zhitom/zkweb


# 使用

打包
mvn -DskipTests clean install -U

放到tomcat下，启动tomcat

启动Tomcat
sh startup.sh 
./catalina.sh run  （建议用这种启动方式，控制信息可以输出）

关闭命令
./shutdown.sh


使用脚本部署

sh deploy.sh



访问
http://localhost:8080/zkWeb-1.0/



