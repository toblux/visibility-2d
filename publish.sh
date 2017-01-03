#!/usr/bin/env bash

# Set errexit to stop immediately when any command returns non-zero.
set -e

# Change to the directory this script file is in.
cd `dirname $0`

if [ ! -d hosted ]; then
  git clone git@github.com:toblux/visibility-2d.git hosted
fi

cd hosted

git checkout gh-pages
git reset --hard
git pull
git rm -rf .

lein cljsbuild once release

# Create directories
mkdir js css

# Copy files
cp ../resources/public/index.html index.html
cp ../resources/public/js/visibility-2d.min.js js/visibility-2d.min.js
cp ../resources/public/css/main.css css/main.css

git add .
git commit -m "Publish"
git push origin gh-pages
