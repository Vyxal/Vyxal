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
function Description({ result, token, name, description, overloads }) {
  const highlightResult = (defaultItem, resultItem) => {
    const highlight =
      resultItem &&
      fuzzysort.highlight(
        resultItem,
        (match, i) => html`<span key=${i} className="highlight">${match}</span>`
      );
    return highlight?.length > 0 ? highlight : defaultItem;
  };
  return html`<div className="description">
    <button class="insertToken" onClick=${() => typeKey(token)}>${token}</button>${" "}
    (${highlightResult(name, result?.[0])})${"\n"}${highlightResult(
      description,
      result?.[1]
    )}${"\n"}${highlightResult(overloads, result?.[2])}
  </div>`;
}

/** Component for rendering the key and its tooltip. */
function Tooltip({
  chr,
  descs,
  results,
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
          offset: [0, 0],
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

  const descriptions = descs?.map((desc, i) => {
    const result = results.find((result) => result.obj.token === desc.token);
    return html`<${Description} key=${i} result=${result} ...${desc} />`;
  });

  const renderTooltip = () => html`
    <div
      className="tooltip"
      ref=${setPopper}
      style=${styles.popper}
      ...${attributes.popper}
    >
      ${descriptions}
      <div className="arrow" ref=${setArrow} style=${styles.arrow} />
    </div>
  `;

  // the "onMouseEnter" and "onMouseLeave" events really mean mouse; they are
  // not triggered by touch screens.

  return html`
    <span ref=${setParent}>
      <span
        onMouseEnter=${() => setLastTouchedKey(chr)}
        onMouseLeave=${() => setLastTouchedKey(null)}
      >
        <${Key} chr=${chr} isFocused=${showTooltip} addRef=${addRef} />
        ${showTooltip && renderTooltip()}
      </span>
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
  /** keyboard ref to attach events manually to */
  const keyboardRef = useRef(null);

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

  // this can't be a normal react event because we want to set passive: false
  useEffect(() => {
    keyboardRef.current.addEventListener("touchmove", touchMove, {
      passive: false,
    });
    return () => {
      keyboardRef.current.removeEventListener("touchmove", touchMove, {
        passive: false,
      });
    };
  }, [touchMove]);

  const touchEnd = () => {
    // this also triggers pointerUp
    setShowTooltips(false);
    setLastTouchedKey(null);
  };

  ////////////
  // search //
  ////////////

  /** list of search results, blank if none */
  const [searchResults, setSearchResults] = useState([]);
  const [query, setQuery] = useState("");
  /** list of targets to search on */
  const targets = useRef(null);
  const keys = ["name", "description", "overloads"];

  useEffect(() => {
    targets.current = Object.entries(codepage_descriptions).flatMap(
      ([index, elts]) => {
        return elts.map((elt) => {
          const result = { index, token: elt.token };
          keys.forEach((key) => (result[key] = fuzzysort.prepare(elt[key])));
          return result;
        });
      }
    );
  }, []);

  useEffect(() => {
    setSearchResults(
      fuzzysort.go(query, targets.current, {
        all: true,
        keys: ["name", "description", "overloads"],
        threshold: -10000,
      })
    );
  }, [query]);

  const renderChildren = () => {
    const keys = [
      ...searchResults
        .reduce((map, result) => {
          if (!map.has(result.obj.index)) map.set(result.obj.index, []);
          map.get(result.obj.index).push(result);
          return map;
        }, new Map())
        .entries(),
    ];
    return keys.map(([i, results]) => {
      const chr = codepage[i];
      return html`<${Tooltip}
        key=${i}
        chr=${chr}
        descs=${codepage_descriptions[i]}
        results=${results}
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
          ref=${keyboardRef}
          style=${{
            touchAction: showTooltips ? "pinch-zoom" : "auto",
            userSelect: isPointerDown ? "none" : "auto",
          }}
          onPointerDown=${pointerDown}
          onPointerUp=${pointerUp}
          onTouchStart=${touchStart}
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
