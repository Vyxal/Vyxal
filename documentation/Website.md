# Developing the Website

Generating `pages/vyxal.js` is already covered in [`BuildTools.md`](./BuildTools.md),
but here it is again:

- Run `mill js.fastLinkJS` to compile JS and produce `pages/vyxal.js`.
- If you want it to automatically rebuild JS every time you make changes, use `-w`: `mill -w js.fastLinkJS`

If you want to test the website, one way is to open up `pages/index.html` directly
in your browser. However, local requests will be blocked, so you can't use
dictionary (de)compression. As a workaround to this, you can run a local server.
One way to do this is with the [`local_server.py`](/local_server.py) script located
in the root folder. You can also run `python3 -m http.server --directory pages` directly.
