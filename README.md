# Log_Monitor

This is to execute any commandline operation (such as capturing dumps, saving a particular line with this keyword into to different file) when there is a matching SearchingKeyword in a log file. 
You can edit config.properties file to pass the below data. Since this has a thread pool mechanism, it will provide a high-performance



tailCommand=tail -f /Users/selakapiumal/.wum3/products/wso2am/4.1.0/wso2am-4.1.0/repository/logs/wso2carbon.log

SearchingKeyword=/newblog/test/1

logFilePath=/Users/selakapiumal/.wum3/products/wso2am/4.1.0/wso2am-4.1.0/repository/logs/wso2carbon.log
