#!/usr/bin/env bash

set -e
cd /home/Vyxal/mysite
git pull
pip install --user --upgrade -r requirements.txt -r requirements_flask_app.txt
curl \
    -X POST \
    -H "Authorization: Token $API_TOKEN" \
    'https://www.pythonanywhere.com/api/v0/user/vyxal/webapps/vyxal.pythonanywhere.com/reload/'
