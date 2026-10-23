# snake.io

A snake.io style game written in Java with Swing. You control a snake with the mouse, eat pellets to grow, and try to stay alive while AI bots roam the arena.

## Requirements

- **Java Development Kit (JDK) 8 or newer.** Check with `javac -version`.
- A computer with a **graphical display**. The game opens a window and will not run on a headless server.
- No external libraries are needed. Everything uses the standard Java library.
- Optional: a file called `face_derp.png` in the project folder to draw a face on the snake's head. If it is missing, the game still runs and prints a warning.

## Project files

| File | Purpose |
|------|---------|
| `main.java` | Entry point (class `Main`), menu and name input |
| `GamePanel.java` | Game loop, collisions, camera, drawing |
| `Snake.java` | Snake movement, growing, boosting, drawing |
| `Bot.java` | A computer-controlled snake |
| `BotStrategy.java` | Interface for bot behaviour |
| `WanderSeekStrategy.java` | Default bot behaviour (chase pellets, otherwise wander) |
| `Food.java` | Pellets dropped by eating or by snakes that die |
| `Leaderboard.java` | Top 5 scoreboard in the corner |
| `highscore.txt` | Saved highscore (created automatically) |

## Building

Open a terminal in the project folder (the folder that contains the `.java` files) and run:

```
javac *.java
```

This creates `.class` files in the same folder.

On Windows PowerShell, if `*.java` does not expand, list the files instead:

```
javac main.java GamePanel.java Snake.java Bot.java BotStrategy.java WanderSeekStrategy.java Food.java Leaderboard.java
```

## Running

```
java Main
```

The game opens full screen. Enter a name (1 to 12 characters) and click **Start snake.io**.

## Controls

- **Mouse:** move the cursor to steer the snake toward it.
- **Space (hold):** boost. The snake moves faster and drops pellets behind it. Boost only works when the snake is longer than 15 segments.
- **Game over:** click **Play Again** to restart or **Menu** to return to the start screen.

## Notes

- Run the game from the project folder. The highscore is read from and saved to `highscore.txt` in the current working directory, and the snake face image is loaded from there too.
- If you rename `main.java` to `Main.java`, the build and run commands above stay the same.
- Compiled `.class` files and `highscore.txt` are runtime files. A `.gitignore` with these lines keeps them out of version control:

```
*.class
highscore.txt
```
