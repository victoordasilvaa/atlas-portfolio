"""Exercise packaged applications against the Compose PostgreSQL database."""
import base64
import json
import os
import time
import urllib.error
import urllib.request

BASE = "http://127.0.0.1:8080/portfolio"


def request(path, method="GET", payload=None, viewer=False, headers=None):
    prefix = "VIEWER" if viewer else "APP"
    credentials = f"{os.environ[prefix + '_USER']}:{os.environ[prefix + '_PASSWORD']}"
    actual_headers = {"Authorization": "Basic " + base64.b64encode(credentials.encode()).decode()}
    if payload is not None:
        actual_headers["Content-Type"] = "application/json"
    actual_headers.update(headers or {})
    req = urllib.request.Request(BASE + path, method=method, headers=actual_headers,
                                 data=None if payload is None else json.dumps(payload).encode())
    try:
        with urllib.request.urlopen(req, timeout=5) as response:
            return response.status, response.read()
    except urllib.error.HTTPError as error:
        return error.code, error.read()


for attempt in range(60):
    try:
        if request("/api/projects")[0] == 200:
            break
    except (OSError, TimeoutError):
        pass
    time.sleep(2)
else:
    raise RuntimeError("Application did not become ready within 120 seconds")

payload = {"name": "Compose verification", "description": "Packaged WAR integration",
           "startDate": "2026-01-01", "expectedEndDate": "2026-04-01",
           "budget": 100000.01, "managerId": 99, "memberIds": [1, 2]}
status, body = request("/api/projects", "POST", payload)
assert status == 201, (status, body)
project = json.loads(body)
assert project["budget"] == "100000.01", project
path = "/api/projects/" + str(project["id"])
assert request(path, viewer=True)[0] == 200
assert request("/api/projects", "POST", payload, viewer=True)[0] == 403
assert request("/api/portfolio/report")[0] == 200
assert request("/")[0] == 200
status, body = request(path, "DELETE", headers={"If-Match": '"' + str(project["version"]) + '"'})
assert status == 204, (status, body)
assert request(path)[0] == 404
print("Compose smoke passed: PostgreSQL, both WARs, authentication, CRUD, report and JSP")
