If you want to test the site locally, you'll need to open it in localhost. That's because some browsers won't allow webworkers to load local files from raw html files.

You can use any framework you want, but it's reccomended to use the `http-server` npm package.

1) Install Node.js
2) `npm install http-server`
3) `cd Vyxal`
4) `http-server`

And that's it - head over to 127.0.0.1:8080/ (or whatever url it gives you) and test away.
