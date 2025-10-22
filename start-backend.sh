#!/bin/bash

echo "================================"
echo "启动幼儿园管理系统后端服务"
echo "================================"

cd backend

echo "正在编译项目..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "编译成功！正在启动服务..."
    java -jar target/kindergarten-management-1.0.0.jar
else
    echo "编译失败，请检查错误信息"
    exit 1
fi
