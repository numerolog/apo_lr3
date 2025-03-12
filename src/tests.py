import requests
import json

endpoint = "http://127.0.0.1:8080"

response = requests.get(f"{endpoint}/schedule_sync?start_date=10.10.2021&end_date=10.10.2022&codes=RUB%20EUR%20CNY")
assert response.status_code == 200, "ожидалось 200"
print("Тест всё ок - пройден")

response = requests.get(f"{endpoint}/schedule_sync?start_date=10.33.2022&end_date=10.10.2022&codes=RUB%20EUR%20CNY")
assert response.status_code == 400, "ожидался badreq"
print("Тест некорректная дата начала - пройден")

response = requests.get(f"{endpoint}/schedule_sync?end_date=10.33.2022&start_date=10.10.2022&codes=RUB%20EUR%20CNY")
assert response.status_code == 400, "ожидался badreq"
print("Тест некорректная дата конца - пройден")

response = requests.get(f"{endpoint}/schedule_sync?end_date=10.33.2022&start_date=10.33.2022&codes=RUB%20EUR%20CNY")
assert response.status_code == 400, "ожидался badreq"
print("Тест некорректная дата начала и конца - пройден")

response = requests.get(f"{endpoint}/schedule_sync?end_date=10.10.2022&start_date=10.10.2022&codes=")
assert response.status_code == 400, "ожидался badreq"
print("Тест пустых кодов - пройден")

response = requests.get(f"{endpoint}/schedule_sync?end_date=10.10.2022&start_date=10.10.2022&codes=CNY%20CNY")
assert response.status_code == 400, "ожидался badreq"
print("Тест дубликатов кодов - пройден")



response = requests.get(f"{endpoint}/stat?start_date=10.33.2022&end_date=10.10.2022&codes=RUB%20EUR%20CNY")
assert response.status_code == 400, "ожидался badreq"
print("Тест статистики некорректная дата начала - пройден")

response = requests.get(f"{endpoint}/stat?end_date=10.33.2022&start_date=10.10.2022&codes=RUB%20EUR%20CNY")
assert response.status_code == 400, "ожидался badreq"
print("Тест статистики некорректная дата конца - пройден")

response = requests.get(f"{endpoint}/stat?end_date=10.33.2022&start_date=10.33.2022&codes=RUB%20EUR%20CNY")
assert response.status_code == 400, "ожидался badreq"
print("Тест статистики некорректная дата начала и конца - пройден")

response = requests.get(f"{endpoint}/stat?end_date=10.33.2022&start_date=10.33.2022&codes=")
assert response.status_code == 400, "ожидался badreq"
print("Тест статистики пустых кодов - пройден")

response = requests.get(f"{endpoint}/stat?end_date=10.10.2022&start_date=10.10.2022&codes=WW")
assert response.status_code == 400, "ожидался badreq"
print("Тест статистики несуществующих кодов - пройден")

response = requests.get(f"{endpoint}/stat?end_date=10.10.2022&start_date=10.10.2022&codes=CNY%20CNY")
assert response.status_code == 400, "ожидался badreq"
print("Тест статистики дубликатов кодов - пройден")

response = requests.get(f"{endpoint}/stat?start_date=10.10.2021&end_date=10.10.2022&codes=RUB%20EUR%20CNY")
assert response.status_code == 200, "ожидалось 200"
   
data = response.json()
assert "list" in data, "нет списка"

cny_data = next((item for item in data["list"] if item["code"] == "CNY"), None)
assert cny_data is not None, "нет CNY"
assert "min" in cny_data and "max" in cny_data and "avg" in cny_data, "нет данных в cny"

assert isinstance(cny_data["min"], (float, int)), "min не число"
assert isinstance(cny_data["max"], (float, int)), "max не число"
assert isinstance(cny_data["avg"], (float, int)), "avg yt xbckj"

print("Тест статистики всё ок - пройден")




