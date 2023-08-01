# Developing the Website

Run `python3 -m http.server --directory pages` to get a local server so that local requests will work.
Try something like [`httpwatcher`](https://pypi.org/project/httpwatcher/) if you
want it to automatically reload whenever you make changes.

Run `mill js.fastLinkJS` to compile JS once. If you want it to automatically
build JS every time you make changes, use `-w`: `mill -w js.fastLinkJS`
