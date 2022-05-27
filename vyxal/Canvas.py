# A custom class to create an ASCII-art canvas, similar to 05AB1E's

from enum import Enum


class Direction(Enum):
    # Enum class for directions
    UP = 0
    UP_RIGHT = 1
    RIGHT = 2
    DOWN_RIGHT = 3
    DOWN = 4
    DOWN_LEFT = 5
    LEFT = 6
    UP_LEFT = 7

    def to_x(dir):
        return [0, 1, 1, 1, 0, -1, -1, -1][dir.value]

    def to_y(dir):
        return [-1, -1, 0, 1, 1, 1, 0, -1][dir.value]


class Canvas:
    def __init__(self, filler=" "):
        # Canvas is a list of rows
        self.filler = filler
        self.canvas = [[filler]]
        self.x = 0
        self.y = 0
        self.start_x = 0
        self.start_y = 0

    def extend(self, length, dir):
        if dir == Direction.RIGHT:
            for item in self.canvas:
                item.extend([self.filler] * length)
        elif dir == Direction.DOWN:
            for _ in range(length):
                self.canvas.append([self.filler] * len(self.canvas[0]))
        elif dir == Direction.LEFT:
            for item, index in enumerate(self.canvas):
                self.canvas[item] = [self.filler] * length + index
            self.x += length
            self.start_x += length
        elif dir == Direction.UP:
            for _ in range(length):
                self.canvas = [
                    [self.filler] * len(self.canvas[0])
                ] + self.canvas
            self.y += length
            self.start_y += length

    def draw_line(self, dir, length, text):
        final_y = self.y + Direction.to_y(dir) * (length - 1)
        if final_y < 0:
            self.extend(-final_y, Direction.UP)
            final_y = 0
        elif final_y >= len(self.canvas):
            self.extend(final_y - len(self.canvas) + 1, Direction.DOWN)
            final_y = len(self.canvas) - 1

        final_x = self.x + Direction.to_x(dir) * (length - 1)
        if final_x < 0:
            self.extend(-final_x, Direction.LEFT)
            final_x = 0
        elif final_x >= len(self.canvas[0]):
            self.extend(final_x - len(self.canvas[0]) + 1, Direction.RIGHT)
            final_x = len(self.canvas[0]) - 1

        for i in range(length):
            self.canvas[self.y + Direction.to_y(dir) * i][
                self.x + Direction.to_x(dir) * i
            ] = text[i % len(text)]

        self.x = final_x
        self.y = final_y

    def draw(self, dirs, lengths, text):
        dirs = Canvas.replace_patterns(dirs)
        maxl = max(len(lengths), len(dirs))
        lsum = None
        for i in range(maxl):
            if dirs[i % len(dirs)] == 8:
                self.x = self.start_x
                self.y = self.start_y
                continue
            l = lengths[i % len(lengths)]

            if lsum == None:
                self.draw_line(Direction(dirs[i % len(dirs)]), l, text)
                lsum = 1
            else:
                self.x += Direction.to_x(Direction(dirs[i % len(dirs)]))
                self.y += Direction.to_y(Direction(dirs[i % len(dirs)]))

                t = text[lsum % len(text) :] + text[: lsum % len(text)]
                self.draw_line(Direction(dirs[i % len(dirs)]), l - 1, t)
            lsum += l - 1

    def __str__(self):
        return "\n".join(["".join(row) for row in self.canvas])

    def replace_patterns(dirs):
        res = []
        for dir in dirs:
            res += {
                "+": [0, 4, 2, 6, 4, 0, 6, 2],
                "x": [1, 5, 3, 7, 5, 1, 7, 3],
                "]": [2, 4, 6],
                "[": [6, 4, 2],
                "<": [5, 3],
                ">": [3, 5],
                "v": [7, 1],
                "^": [1, 7],
            }.get(dir, [dir])
        return res
