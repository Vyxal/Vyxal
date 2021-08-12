"""
File: context.py
Description: This is for context-related stuff.
"""


class Context:
    def __init__(self):
        self.stack = []
        self.context_values = [0]
        self.inputs = []


ctx = Context()
