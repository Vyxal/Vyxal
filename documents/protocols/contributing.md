# The Correct Way to Contribute to Vyxal

The fact that you even considered contributing to Vyxal is very cool and epic. Thank you. Just make sure you follow this guide to contributing and everyone will be
very happy with what you do.

## Preface

Whenever you contribute anything, make sure that you are abiding by the Vyxal [Code of Conduct](https://github.com/Vyxal/Vyxal/blob/Fresh-Beginnings/documents/protocols/CoC.md). Doing so makes everyone's life easier and it makes you an absolute pogchamp.

Also, this whole entire thing (except for the above sentence) is a guide. It does not need to be followed (except for the above sentence) exactly, but it _should_
influence how you contribute.

Finally, as a general principle, decisions should not be made, PRs should not be merged, issues should not be closed or perhaps even worked on, until several people are agreed and all discussion is at least somewhat finalised. Take things a bit slower!



Right. On to the fun stuff.

## What to do when...

### You have a question

Instead of opening an issue, you should go to the [Vyxal chatroom](https://chat.stackexchange.com/rooms/106764/vyxal) and ask your question there. You'll recieve an
answer much quicker than if you open an issue. Plus, we have cool stuff in the chatroom that can help out with your question.

### You found a bug that isn't a security exploit

Open an issue. When opening an issue, make sure that you:

- Provide sample programs demonstrating the issue. If what you're raising is something not code related, provide screenshots or anecdotal evidence where relevant.
- Have a clear issue title
- Describe the problem clearly and with enough detail that anyone reading it can understand what you mean.
- Check to see if a similar issue exists.

Also, when opening an issue, it'd be really cool if you:

- Test it online and offline
- Specify potential commits/versions which are effected
- Add relevant tags to the issue

In summary: provide helpful information in your issue and we'll be happy

### You found a bug that _is_ a security exploit

Do **not** open an issue. We do not need trolls epicly trolling us.

**TODO**: Figure out how to actually report security exploits

### You have a feature request

Open an issue and tag it `enhancement`. When doing so, make sure you:

- Describe what it is you want. Providing examples (e.g. hypothetical programs/mockups) is really cool and good in this regard.
- Be reasonably specific. Avoid being vague. However, being unsure of some (or most) of the implications/logisistics is fine, so long as we can understand the general idea you want.
- Don't request things that already exist.
- Don't request things that wouldn't be reasonable for a golfing language or a practical language.

### You have a complaint, and...

#### You're caird, redwolf, rak1507 or N3buchadnezza

Place your complaints [here](https://chat.stackexchange.com/rooms/82806/trash).

#### You're a Vyxal regular or someone genuinely interested in Vyxal

Tell us in the Vyxal chatroom, and we'll respond accordingly.

### You want to make a Pull Request

Wow. You actually took the time to dive into Vyxal source code and make a meaningful change. Aren't you an epic gamer. Before you merge your changes, make sure to:

- Clearly describe what it is that you are changing.
- Get approval from two other contributors (via requesting a review)
- Check that the PR is actually mergable
- Ensure that it passes the testing workflow

### You are doing something not listed here

Ask in the Vyxal chatroom. We're happy to direct you to the right place.


## Styleguides

Around here, we use black for linting python.

When creating a commit message:

- Use the present tense ("Add feature" not "Added feature")
- Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit the first line to 72 characters or less
- Reference issues and pull requests liberally after the first line
- Consider starting the commit message with a relevant emoji.
