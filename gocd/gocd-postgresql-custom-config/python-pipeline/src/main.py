class HelloWorld:
    def __init__(self, name):
        self.name = name

    def say_hello(self):
        return "Hello, {0}!".format(self.name)
