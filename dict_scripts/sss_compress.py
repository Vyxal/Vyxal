# https://tio.run/##jVTNbttGED5LTzGND9yNKUGSkRSVw0MR9OBeW9QHRQhW5EhahdwldleSpSBBHqR5jVx6y6PkRdyZJSWq9qWwYAxn5@ebb37qQ1hbc/O4dLaCDZblAXRVWxcgtwW@r9UKUyh0HrQ1yh0er0hvfFAmDEHgQ15uvd6h7KMp3pdoIIMJXEOlHgR9ib2EpXWwB20uggxLa1YSrsBYMIgFBBuj6gLdpZlfM44F5mrrEexip@3Wl4cUdEg8xFd0/f4V3JncoSIbXC51rtHkB9hrKmwbuJoSKzRBmxUoCE4jCMLkwxq8rnSpnOw/S5rBx90UdESvU9hxAWi2FToVUDw1l5/6T6qD/x8hmlOEfl4q7@GtrWqH3otS@yCn/V6BS8jXyqmcqhXHFHLW9jRpOWbyzrx7@Hn5/VvC2p6lxL@8IglLskjo701GhvQv@dxZWFeIXA5uJtHQY3xwShOFf6lyi785Z53IqZMvQHtqEpFJvamdNkEtSoRf/3h7d0f9c0RpqQ0uqY3DF5LCHBnA65fHa9t@3JA8Itlh2DoDx35TUscA17RPKZjBqg6HWF3w5HnSNNXuZ9PxHLKMi5rSSGWkGU/nt9DaBhLIssi5e88aSgHagXwDr4FrfjqQ5Hufgs/JWxCckWSfZnIpZPQQEcPQ71Wd07gJSQRFECmMZYPyPiJpnKbwjNJ9S6lJohGxegkjErgkAFQSj2h@CxuGM2EohCyCGEdgZBG/Rrc0Yxlnm93P@y3lXClpJDGvW9WEZRNER0ILeBl7f2rU5vLjP9NxUo6ft3JlxTF2LW9nl1Y6g9mcNPu1pmk5NhFodGNrdpUtxBEGME5h8mrEl@BP9YGOhza@1rQfRAfEg/Q7HyTa9YXeILG0Q1jwmtOx2KHzbKbKlXU0nRVn6PIPVV3TSRLnIzbL57JDnvz48vfH0afv/yRD2s9KBZEkw43VRnQhZtPpYDyX5NVUaeugK1UKH1wstqi5yNEcXsLgM9PKD9w/XnhurlNmheL0AoNxGn/Rmbxnmqb5vO7DbsX5icZkTFNFjmTGUWPYTReWqKsIbux0TcH5xrQ3WDYZesEdGuGcjD2inHZ5L/aQn643p7TTRtbwU0bLEDH06OBjHS4Guk1Q0@ViqWOYU81Y1VL948vXM9dMR8pvZxA0QZR8NI9qSvoBDxlX8hgvjjjbnXpgqbliJId09QshpXzcrgL64Mvt6l8


def g(s, d):
    code_page = """¡¢£¤¥¦©¬®µ½¿€ÆÇÐÑ×ØŒÞßæçðıȷñ÷øœþ !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~¶"""
    code_page += """°¹²³⁴⁵⁶⁷⁸⁹⁺⁻⁼⁽⁾ƁƇƊƑƓƘⱮƝƤƬƲȤɓƈɗƒɠɦƙɱɲƥʠɼʂƭʋȥẠḄḌẸḤỊḲḶṂṆỌṚṢṬỤṾẈỴẒȦḂĊḊĖḞĠḢİĿṀṄȮṖṘṠṪẆẊẎŻạḅḍẹḥịḳḷṃṇọṛṣṭ§Äẉỵẓȧḃċḋėḟġḣŀṁṅȯṗṙṡṫẇẋẏż«»‘’“”"""

    # constant. (exclusive)
    end_len = 2 + max(
        len(w) for w in d["long"]
    )  # no need to consider dictionary.short because obviously, it's shorter

    # Increase efficiency without implementing a trie (or sth similar)
    d["short"] = {v: i for i, v in enumerate(d["short"])}
    d["long"] = {v: i for i, v in enumerate(d["long"])}

    def character(z, c):
        if c in "\n\x7f¶":
            o = 95
        elif " " <= c <= "~":
            o = ord(c) - 32
        else:
            raise ValueError(c + " is neither printable ASCII nor a linefeed.")
        z = 96 * z + o
        z = 3 * z + 0
        return z

    def dictionary(z, w, nonempty):
        ts = nonempty
        if w[:1] == " ":
            w = w[1:]
            ts = not ts
        dct = d["short"] if len(w) < 6 else d["long"]
        W, sc = (w, 0) if w in dct else (w[:1].swapcase() + w[1:], 1)
        if W not in dct:
            raise ValueError(w + " isn't in the dictionary.")
        f = ts or sc
        j = (2 if sc else 1) if ts else 0
        i = dct[W]

        z = len(dct) * z + i
        z = 2 * z + int(len(w) < 6)
        if f:
            z = 3 * z + j
            z = 3 * z + 2
        else:
            z = 3 * z + 1
        return z

    def go(z):
        compressed = []
        while z:
            z, c = divmod(
                z - 1, 252
            )  # Taken inspiration from Jelly's bijective base conversion algorithm
            compressed.append(code_page[c])
        return "“{0}»".format("".join(compressed[::-1]))

    def optimal(str):
        dp = [0] * (len(str) + 1)
        for i in range(len(str) - 1, -1, -1):
            dp[i] = character(dp[i + 1], str[i])
            for j in range(1, min(len(dp) - i, end_len)):
                try:
                    dp[i] = min(
                        dp[i],
                        dictionary(dp[i + j], str[i : i + j], i != 0),
                    )
                except ValueError:
                    pass

        return min(
            [
                "“{0}”".format(str),
                go(dp[0]),
            ],
            key=len,
        )

    return optimal(s)
