import unittest

from .main import HelloWorld


class HelloWorldTestCase(unittest.TestCase):
    def setUp(self):
        self.hello_world = HelloWorld("World")

    def test_something(self):
        self.assertEqual(self.hello_world.say_hello(), "Hello, World!")


if __name__ == '__main__':
    unittest.main()
