interface BotStrategy {
    double[] decide(Snake self, Iterable<Food> pellets);
}