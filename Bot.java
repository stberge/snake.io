class Bot {
    private final String name;
    private final Snake snake;
    private final BotStrategy strategy;

    Bot(String name, double x, double y, BotStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
        snake = new Snake(x, y, true, Snake.randomColor());
    }

    String getName() {
        return name;
    }

    Snake getSnake() {
        return snake;
    }

    void update(Iterable<Food> pellets) {
        double[] target = strategy.decide(snake, pellets);
        snake.updateAndDrop(target[0], target[1]);
    }
}