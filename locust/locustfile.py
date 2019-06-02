import random

import requests
from locust import HttpLocust, seq_task, TaskSequence, task

stock_items = []


def create_stock_items(l):
    for i in range(1, 20):
        response = requests.post(l.host + "/stock", {
            "stock": 15000000,
            "name": "TurboKafka2000",
            "price": 1240
        })
        stock_items.append(response.json()['id'])


class MyTaskSequence(TaskSequence):
    @seq_task(1)
    def create_user(self):
        response = self.client.post("/users", {
            "firstName": "Casper",
            "lastName": "Boone",
            "street": "Mekelpark",
            "zip": "2142AB",
            "city": "Delft"
        })
        self.user_id = response.json()['id']

    @seq_task(2)
    def create_order(self):
        response = self.client.post("/orders/" + self.user_id)
        self.order_id = response.json()['id']

    @seq_task(3)
    @task(4)  # add four items
    def add_order_item(self):
        self.client.post("/orders/" + self.order_id + "/items", {
            "itemId": random.choice(stock_items)
        })

    @seq_task(4)
    def checkout_order(self):
        self.client.post("/orders/" + self.order_id + "/checkout")


class WebsiteUser(HttpLocust):
    task_set = MyTaskSequence
    min_wait = 5000
    max_wait = 9000

    def setup(self):
        create_stock_items(self)
