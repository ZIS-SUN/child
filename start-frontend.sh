#!/bin/bash

echo "================================"
echo "启动幼儿园管理系统前端服务"
echo "================================"

cd frontend

if [ ! -d "node_modules" ]; then
    echo "正在安装依赖..."
    npm install
fi

echo "正在启动前端服务..."
npm run dev
