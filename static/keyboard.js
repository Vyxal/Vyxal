const html = htm.bind(React.createElement);
const { createRoot } = ReactDOM;
const { useState, useEffect, useRef } = React;
const { usePopper } = ReactPopper;

function throttle(func, timeFrame) {
  let lastTime = 0;
  return (...args) => {
    const now = Date.now();
    if (now - lastTime >= timeFrame) {
      func(...args);
      lastTime = now;
    }
  };
}

const onMobile = window.matchMedia("(any-hover: none)").matches;

const typeKey = (chr) => {
  const cm = globalThis[`e_${selectedBox}`];
  if (!cm || !chr) return;
  cm.replaceSelection(chr);
  cm.save();
  if (!onMobile) {
    cm.focus();
  }
  updateCount();
};

/** Component for rendering the key proper. */
function Key({ chr, isFocused, addRef }) {
  const key = useRef(null);

  useEffect(() => {
    addRef(key.current);
  }, []);

  const pointerUp = () => {
    // if on mobile, this is handled by the keyboard's event
    if (!onMobile) typeKey(chr);
  };

  return html`<span
    ref=${key}
    className=${isFocused ? "key touched" : "key"}
    onPointerUp=${pointerUp}
  >
    ${chr}
  </span>`;
}

/** Component for rendering a single token of a tooltip */
function Description({ token, name, description, overloads }) {
  return html`<div className="description">
    ${token} (${name})${"\n"}${description}${"\n"}${overloads}
  </div>`;
}

/** Component for rendering the key and its tooltip. */
function Tooltip({
  shown,
  chr,
  descs,
  setLastTouchedKey,
  showTooltip,
  addRef,
}) {
  const [parent, setParent] = useState(null);
  const [popper, setPopper] = useState(null);
  const [arrow, setArrow] = useState(null);

  const { styles, attributes } = usePopper(parent, popper, {
    modifiers: [
      {
        name: "arrow",
        options: { element: arrow },
      },
      {
        name: "offset",
        options: {
          offset: [0, 8],
        },
      },
      {
        name: "preventOverflow",
        options: {
          padding: 10,
        },
      },
    ],
  });

  const descriptions = descs?.map(
    (desc, i) => html`<${Description} key=${i} ...${desc} />`
  );

  const renderTooltip = () => html`
    <div
      className="tooltip"
      ref=${setPopper}
      data-show=${showTooltip}
      style=${styles.popper}
      ...${attributes.popper}
    >
      ${descriptions}
      <div className="arrow" ref=${setArrow} style=${styles.arrow} />
    </div>
  `;

  // render the element no matter what, just change its display, as rerendering
  // is expensive / leads to weird repainting for tooltips.

  // the "onMouseEnter" and "onMouseLeave" events really mean mouse; they are
  // not triggered by touch screens.

  return html`
    <span ref=${setParent} style=${{ display: shown ? "inline" : "none" }}>
      <span
        onMouseEnter=${() => setLastTouchedKey(chr)}
        onMouseLeave=${() => setLastTouchedKey(null)}
      >
        <${Key} chr=${chr} isFocused=${showTooltip} addRef=${addRef} />
      </span>
      ${showTooltip && renderTooltip()}
    </span>
  `;
}

function Keyboard() {
  //////////////
  // tooltips //
  //////////////

  const [isPointerDown, setIsPointerDown] = useState(false);
  const [showTooltips, setShowTooltips] = useState(!onMobile);
  const [lastTouchedKey, setLastTouchedKey] = useState(null);
  /** list of key elements, to be used to check which one we're hovering */
  const keyElts = useRef([]);
  /** timeout for controlling press and hold delay */
  const timeout = useRef(null);

  const suppressContext = (e) => e.preventDefault();

  // don't show the context menu if we're clicking/touching the keyboard
  useEffect(() => {
    if (isPointerDown) {
      window.addEventListener("contextmenu", suppressContext);
    } else {
      window.removeEventListener("contextmenu", suppressContext);
    }
  }, [isPointerDown]);

  const pointerDown = () => {
    if (!onMobile) return;
    setIsPointerDown(true);
    // after 1000 ms, start showing tooltips
    timeout.current = setTimeout(() => {
      setShowTooltips(true);
    }, 1000);
  };

  const pointerUp = () => {
    if (!onMobile) return;
    typeKey(lastTouchedKey);
    setIsPointerDown(false);
    setShowTooltips(false);
    clearTimeout(timeout.current);
  };

  useEffect(() => {
    // if we press down, but then scroll, we don't want to show tooltips
    document.addEventListener("scroll", pointerUp);
  }, []);

  // e is a TouchEvent
  const updateLastTouchedKey = throttle((e) => {
    if (!e) return;
    const { clientX, clientY } = e.touches[0];

    const someTouching = keyElts.current.some(({ chr, elt }) => {
      const { top, right, bottom, left } = elt.getBoundingClientRect();
      const isTouching =
        left <= clientX &&
        clientX <= right &&
        top <= clientY &&
        clientY <= bottom;
      if (isTouching) setLastTouchedKey(chr);
      return isTouching;
    });

    if (!someTouching) setLastTouchedKey(null);
  }, 100);

  const touchStart = (e) => {
    // this also triggers pointerDown
    updateLastTouchedKey(e);
  };

  const touchMove = (e) => {
    updateLastTouchedKey(e);
    if (showTooltips) e.preventDefault();
  };

  const touchEnd = () => {
    // this also triggers pointerUp
    setShowTooltips(false);
    setLastTouchedKey(null);
  };

  ////////////
  // search //
  ////////////

  const [shownIndexes, setShownIndexes] = useState([]);
  const [query, setQuery] = useState("");
  const targets = useRef(null);
  const allIndexes = useRef([]);
  const keys = ["name", "description", "overloads"];

  useEffect(() => {
    const indexes = [];
    targets.current = Object.entries(codepage_descriptions).flatMap(
      ([index, elts]) => {
        indexes.push(index.toString());
        return elts.map((elt) => {
          const result = { index };
          keys.forEach((key) => (result[key] = fuzzysort.prepare(elt[key])));
          return result;
        });
      }
    );
    allIndexes.current = [...new Set(indexes)];
  }, []);

  useEffect(() => {
    if (query) {
      const results = fuzzysort.go(query, targets.current, {
        keys: ["name", "description", "overloads"],
      });
      setShownIndexes([...new Set(results.map((result) => result.obj.index))]);
    } else {
      setShownIndexes(allIndexes.current);
    }
  }, [query]);

  const renderChildren = () => {
    return [...shownIndexes].map((i) => {
      const chr = codepage[i];
      return html`<${Tooltip}
        key=${i}
        shown=${shownIndexes.includes(i)}
        chr=${chr}
        descs=${codepage_descriptions[i]}
        setLastTouchedKey=${setLastTouchedKey}
        showTooltip=${showTooltips && chr === lastTouchedKey}
        addRef=${(elt) => keyElts.current.push({ chr, elt })}
      />`;
    });
  };

  const ELEMENTS_LINK =
    "https://github.com/Vyxal/Vyxal/blob/main/documents/knowledge/elements.md";

  return html`
    <div className="row">
      <label htmlFor="filterBox"
        >Search <a href=${ELEMENTS_LINK}>elements</a>:
      </label>
      <input
        label="Search elements:"
        onInput=${(e) => setQuery(e.target.value)}
      />
      <div className="twelve columns">
        <div
          id="keyboard"
          style=${{
            touchAction: showTooltips ? "pinch-zoom" : "auto",
            userSelect: isPointerDown ? "none" : "auto",
          }}
          onPointerDown=${pointerDown}
          onPointerUp=${pointerUp}
          onTouchStart=${touchStart}
          onTouchMove=${touchMove}
          onTouchEnd=${touchEnd}
          onTouchCancel=${touchEnd}
        >
          ${renderChildren()}
        </div>
      </div>
    </div>
  `;
}

window.addEventListener("DOMContentLoaded", () => {
  const kb = document.getElementById("keyboard-root");
  const root = createRoot(kb);
  root.render(html`<${Keyboard} />`);
});
