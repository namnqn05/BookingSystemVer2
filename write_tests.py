import os

BASE = r"postman\collections\Booking System API"

files = {
    # ── Auth ──────────────────────────────────────────────────────────────────
    r"Auth\Login.request.yaml": """\
$kind: http-request
name: Login
method: POST
url: '{{baseUrl}}/api/auth/login'
order: 1000
headers:
  - key: Content-Type
    value: application/json
body:
  type: json
  content: |-
    {
      "email": "admin@example.com",
      "password": "password123"
    }
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      if (json.accessToken) {
        pm.environment.set('token', json.accessToken);
      }
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response has accessToken', () => { pm.expect(json.accessToken).to.be.a('string').and.not.empty; });
      pm.test('tokenType is Bearer', () => { pm.expect(json.tokenType).to.eql('Bearer'); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
      pm.test('Response has email', () => { pm.expect(json.email).to.be.a('string').and.not.empty; });
      pm.test('Response has fullName', () => { pm.expect(json.fullName).to.be.a('string').and.not.empty; });
      pm.test('Response has role', () => { pm.expect(json.role).to.be.a('string').and.not.empty; });
      pm.test('Token saved to environment', () => { pm.expect(pm.environment.get('token')).to.be.a('string').and.not.empty; });
""",

    r"Auth\Register.request.yaml": """\
$kind: http-request
method: POST
url: '{{baseUrl}}/api/auth/register'
order: 2000
headers:
  - key: Content-Type
    value: application/json
body:
  type: json
  content: |-
    {
      "email": "user@example.com",
      "password": "password123",
      "fullName": "John Doe",
      "role": "USER",
      "departmentId": 1
    }
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 201', () => { pm.response.to.have.status(201); });
      pm.test('Response has accessToken', () => { pm.expect(json.accessToken).to.be.a('string').and.not.empty; });
      pm.test('tokenType is Bearer', () => { pm.expect(json.tokenType).to.eql('Bearer'); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
      pm.test('Response has email', () => { pm.expect(json.email).to.be.a('string').and.not.empty; });
      pm.test('Response has fullName', () => { pm.expect(json.fullName).to.be.a('string').and.not.empty; });
      pm.test('Response has role', () => { pm.expect(json.role).to.be.a('string').and.not.empty; });
""",

    # ── Account ───────────────────────────────────────────────────────────────
    r"Account\Get Account.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/account'
order: 1000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
      pm.test('Response has email', () => { pm.expect(json.email).to.be.a('string').and.not.empty; });
      pm.test('Response has fullName', () => { pm.expect(json.fullName).to.be.a('string').and.not.empty; });
      pm.test('Response has activated field', () => { pm.expect(json).to.have.property('activated'); });
      pm.test('Response has role array', () => { pm.expect(json.role).to.be.an('array').and.not.empty; });
""",

    r"Account\Update Account.request.yaml": """\
$kind: http-request
method: PUT
url: '{{baseUrl}}/api/account'
order: 2000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
headers:
  - key: Content-Type
    value: application/json
body:
  type: json
  content: |-
    {
      "fullName": "John Doe",
      "email": "user@example.com"
    }
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
      pm.test('Response has email', () => { pm.expect(json.email).to.be.a('string').and.not.empty; });
      pm.test('Response has fullName', () => { pm.expect(json.fullName).to.be.a('string').and.not.empty; });
""",

    r"Account\Get Invoices.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/account/invoices'
order: 3000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
queryParams:
  - key: q
    value: ''
    description: Optional search query
    disabled: true
  - key: page
    value: '0'
  - key: size
    value: '10'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response is paginated (has content array)', () => { pm.expect(json.content).to.be.an('array'); });
      pm.test('Response has totalElements', () => { pm.expect(json).to.have.property('totalElements'); });
      pm.test('Response has totalPages', () => { pm.expect(json).to.have.property('totalPages'); });
""",

    r"Account\Export Invoices CSV.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/account/invoices/export'
order: 4000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response is CSV (text/csv or application/octet-stream)', () => {
        const ct = pm.response.headers.get('Content-Type') || '';
        pm.expect(ct).to.match(/text\/csv|application\/octet-stream|text\/plain/i);
      });
""",

    # ── Bookings ──────────────────────────────────────────────────────────────
    r"Bookings\Get All Bookings.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/bookings'
order: 1000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
queryParams:
  - key: date
    value: '2026-08-11'
    description: 'Optional date filter (e.g. 2024-01-15)'
    disabled: true
  - key: page
    value: '0'
  - key: size
    value: '10'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response is paginated (has content array)', () => { pm.expect(json.content).to.be.an('array'); });
      pm.test('Response has totalElements', () => { pm.expect(json).to.have.property('totalElements'); });
      if (json.content.length > 0) {
        const b = json.content[0];
        pm.test('Booking has id', () => { pm.expect(b.id).to.be.a('number'); });
        pm.test('Booking has roomId', () => { pm.expect(b.roomId).to.be.a('number'); });
        pm.test('Booking has status', () => { pm.expect(b.status).to.be.a('string'); });
        pm.test('Booking has title', () => { pm.expect(b.title).to.be.a('string'); });
      }
""",

    r"Bookings\Create Booking.request.yaml": """\
$kind: http-request
method: POST
url: '{{baseUrl}}/api/bookings'
order: 2000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
headers:
  - key: Content-Type
    value: application/json
body:
  type: json
  content: |-
    {
      "roomId": 1,
      "title": "Team Meeting",
      "startTime": "2026-08-11T13:00:00",
      "endTime": "2026-08-11T14:00:00"
    }
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 201', () => { pm.response.to.have.status(201); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
      pm.test('Response has roomId', () => { pm.expect(json.roomId).to.be.a('number'); });
      pm.test('Response has title', () => { pm.expect(json.title).to.be.a('string').and.not.empty; });
      pm.test('Response has status', () => { pm.expect(json.status).to.be.a('string'); });
      pm.test('Response has startTime', () => { pm.expect(json.startTime).to.exist; });
      pm.test('Response has endTime', () => { pm.expect(json.endTime).to.exist; });
      if (json.id) { pm.environment.set('bookingId', json.id); }
""",

    r"Bookings\Approve Booking.request.yaml": """\
$kind: http-request
name: 'Approve Booking'
method: POST
url: '{{baseUrl}}/api/bookings/:id/approve'
order: 3000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
pathVariables:
  - key: id
    value: '1'
    description: Booking ID (Admin only)
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Booking status is APPROVED', () => { pm.expect(json.status).to.eql('APPROVED'); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
""",

    r"Bookings\Reject Booking.request.yaml": """\
$kind: http-request
name: 'Reject Booking'
method: POST
url: '{{baseUrl}}/api/bookings/:id/reject'
order: 4000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
pathVariables:
  - key: id
    value: '1'
    description: Booking ID (Admin only)
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Booking status is REJECTED', () => { pm.expect(json.status).to.eql('REJECTED'); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
""",

    r"Bookings\Cancel Booking.request.yaml": """\
$kind: http-request
name: 'Cancel Booking'
method: POST
url: '{{baseUrl}}/api/bookings/:id/cancel'
order: 5000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
pathVariables:
  - key: id
    value: '1'
    description: Booking ID (Admin only)
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Booking status is CANCELLED', () => { pm.expect(json.status).to.eql('CANCELLED'); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
""",

    # ── Rooms ─────────────────────────────────────────────────────────────────
    r"Rooms\Get All Rooms.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/rooms'
order: 1000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
queryParams:
  - key: page
    value: '0'
  - key: size
    value: '10'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response is paginated (has content array)', () => { pm.expect(json.content).to.be.an('array'); });
      pm.test('Response has totalElements', () => { pm.expect(json).to.have.property('totalElements'); });
      if (json.content.length > 0) {
        const r = json.content[0];
        pm.test('Room has id', () => { pm.expect(r.id).to.be.a('number'); });
        pm.test('Room has name', () => { pm.expect(r.name).to.be.a('string').and.not.empty; });
        pm.test('Room has capacity', () => { pm.expect(r.capacity).to.be.a('number'); });
      }
""",

    r"Rooms\Create Room.request.yaml": """\
$kind: http-request
method: POST
url: '{{baseUrl}}/api/rooms'
order: 2000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
headers:
  - key: Content-Type
    value: application/json
body:
  type: json
  content: |-
    {
      "name": "Conference Room A",
      "capacity": 10,
      "isActive": true
    }
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 201', () => { pm.response.to.have.status(201); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
      pm.test('Response has name', () => { pm.expect(json.name).to.be.a('string').and.not.empty; });
      pm.test('Response has capacity', () => { pm.expect(json.capacity).to.be.a('number'); });
      pm.test('Response has isActive', () => { pm.expect(json).to.have.property('isActive'); });
      if (json.id) { pm.environment.set('roomId', json.id); }
""",

    r"Rooms\Update Room.request.yaml": """\
$kind: http-request
name: 'Update Room'
method: PATCH
url: '{{baseUrl}}/api/rooms/:id'
order: 3000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
pathVariables:
  - key: id
    value: '1'
    description: Room ID (Admin only)
headers:
  - key: Content-Type
    value: application/json
body:
  type: json
  content: |-
    {
      "name": "Updated Room",
      "capacity": 15,
      "isActive": true
    }
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
      pm.test('Response has name', () => { pm.expect(json.name).to.be.a('string').and.not.empty; });
      pm.test('Response has capacity', () => { pm.expect(json.capacity).to.be.a('number'); });
""",

    # ── Admin / Users ─────────────────────────────────────────────────────────
    r"Admin\Users\Get All Users.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/admin/users'
order: 1000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
queryParams:
  - key: q
    value: ''
    description: Optional search query
    disabled: true
  - key: activated
    value: 'true'
    description: Optional boolean filter
    disabled: true
  - key: page
    value: '0'
  - key: size
    value: '10'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response is paginated (has content array)', () => { pm.expect(json.content).to.be.an('array'); });
      pm.test('Response has totalElements', () => { pm.expect(json).to.have.property('totalElements'); });
      if (json.content.length > 0) {
        const u = json.content[0];
        pm.test('User has id', () => { pm.expect(u.id).to.be.a('number'); });
        pm.test('User has email', () => { pm.expect(u.email).to.be.a('string').and.not.empty; });
        pm.test('User has fullName', () => { pm.expect(u.fullName).to.be.a('string'); });
      }
""",

    r"Admin\Users\Create User.request.yaml": """\
$kind: http-request
method: POST
url: '{{baseUrl}}/api/admin/users'
order: 2000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
headers:
  - key: Content-Type
    value: application/json
body:
  type: json
  content: |-
    {
      "email": "newuser@example.com",
      "password": "password123",
      "fullName": "Jane Doe",
      "role": "USER",
      "activated": true,
      "departmentId": 1
    }
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 201', () => { pm.response.to.have.status(201); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
      pm.test('Response has email', () => { pm.expect(json.email).to.be.a('string').and.not.empty; });
      pm.test('Response has fullName', () => { pm.expect(json.fullName).to.be.a('string').and.not.empty; });
      pm.test('Response has role', () => { pm.expect(json.role).to.exist; });
      if (json.id) { pm.environment.set('userId', json.id); }
""",

    r"Admin\Users\Update User.request.yaml": """\
$kind: http-request
name: 'Update User'
method: PUT
url: '{{baseUrl}}/api/admin/users/:id'
order: 3000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
pathVariables:
  - key: id
    value: '1'
    description: User ID
headers:
  - key: Content-Type
    value: application/json
body:
  type: json
  content: |-
    {
      "email": "user@example.com",
      "fullName": "Jane Doe",
      "role": "USER",
      "activated": true,
      "departmentId": 1
    }
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
      pm.test('Response has email', () => { pm.expect(json.email).to.be.a('string').and.not.empty; });
      pm.test('Response has fullName', () => { pm.expect(json.fullName).to.be.a('string').and.not.empty; });
""",

    r"Admin\Users\Deactivate User.request.yaml": """\
$kind: http-request
name: 'Deactivate User'
method: PATCH
url: '{{baseUrl}}/api/admin/users/:id/deactivate'
order: 4000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
pathVariables:
  - key: id
    value: '1'
    description: User ID
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      const json = pm.response.json();
      pm.test('User is deactivated', () => { pm.expect(json.activated).to.eql(false); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
""",

    # ── Admin / Revenue ───────────────────────────────────────────────────────
    r"Admin\Revenue\Get Revenue.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/admin/revenue'
order: 1000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
queryParams:
  - key: yearMonth
    value: '2026-08'
    description: 'Optional year-month filter (e.g. 2024-01)'
    disabled: true
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response is an array of revenue periods', () => { pm.expect(json).to.be.an('array'); });
      if (json.length > 0) {
        const r = json[0];
        pm.test('Revenue period has yearMonth', () => { pm.expect(r.yearMonth).to.be.a('string'); });
        pm.test('Revenue period has totalAmount', () => { pm.expect(r).to.have.property('totalAmount'); });
        pm.test('Revenue period has totalBookings', () => { pm.expect(r).to.have.property('totalBookings'); });
      }
""",

    r"Admin\Revenue\Get Revenue By Room.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/admin/revenue/rooms'
order: 2000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
queryParams:
  - key: yearMonth
    value: '2026-08'
    description: 'Optional year-month filter (e.g. 2024-01)'
    disabled: true
  - key: q
    value: ''
    description: Optional search query
    disabled: true
  - key: page
    value: '0'
  - key: size
    value: '10'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response is paginated (has content array)', () => { pm.expect(json.content).to.be.an('array'); });
      pm.test('Response has totalElements', () => { pm.expect(json).to.have.property('totalElements'); });
""",

    r"Admin\Revenue\Export Revenue CSV.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/admin/revenue/export'
order: 3000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
queryParams:
  - key: yearMonth
    value: '2026-08'
    description: 'Optional year-month filter (e.g. 2024-01)'
    disabled: true
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response is CSV', () => {
        const ct = pm.response.headers.get('Content-Type') || '';
        pm.expect(ct).to.match(/text\\/csv|application\\/octet-stream|text\\/plain/i);
      });
""",

    # ── Admin / Department Change Requests ────────────────────────────────────
    r"Admin\Department Change Requests\Get All Requests.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/admin/department-change-requests'
order: 1000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
queryParams:
  - key: status
    value: PENDING
    description: 'Optional status filter (e.g. PENDING)'
    disabled: true
  - key: page
    value: '0'
  - key: size
    value: '10'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response is paginated (has content array)', () => { pm.expect(json.content).to.be.an('array'); });
      pm.test('Response has totalElements', () => { pm.expect(json).to.have.property('totalElements'); });
      if (json.content.length > 0) {
        const r = json.content[0];
        pm.test('Request has id', () => { pm.expect(r.id).to.be.a('number'); });
        pm.test('Request has status', () => { pm.expect(r.status).to.be.a('string'); });
      }
""",

    r"Admin\Department Change Requests\Approve Request.request.yaml": """\
$kind: http-request
name: 'Approve Request'
method: POST
url: '{{baseUrl}}/api/admin/department-change-requests/:id/approve'
order: 2000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
pathVariables:
  - key: id
    value: '1'
    description: Department Change Request ID
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Request status is APPROVED', () => { pm.expect(json.status).to.eql('APPROVED'); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
""",

    r"Admin\Department Change Requests\Reject Request.request.yaml": """\
$kind: http-request
name: 'Reject Request'
method: POST
url: '{{baseUrl}}/api/admin/department-change-requests/:id/reject'
order: 3000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
pathVariables:
  - key: id
    value: '1'
    description: Department Change Request ID
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Request status is REJECTED', () => { pm.expect(json.status).to.eql('REJECTED'); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
""",

    # ── Notifications ─────────────────────────────────────────────────────────
    r"Notifications\Get Notifications.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/notifications'
order: 1000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
queryParams:
  - key: page
    value: '0'
  - key: size
    value: '10'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response is paginated (has content array)', () => { pm.expect(json.content).to.be.an('array'); });
      pm.test('Response has totalElements', () => { pm.expect(json).to.have.property('totalElements'); });
      if (json.content.length > 0) {
        const n = json.content[0];
        pm.test('Notification has id', () => { pm.expect(n.id).to.be.a('number'); });
        pm.test('Notification has title', () => { pm.expect(n.title).to.be.a('string'); });
        pm.test('Notification has isRead field', () => { pm.expect(n).to.have.property('read'); });
      }
""",

    r"Notifications\Mark Notification Read.request.yaml": """\
$kind: http-request
name: 'Mark Notification Read'
method: POST
url: '{{baseUrl}}/api/notifications/:id/read'
order: 2000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
pathVariables:
  - key: id
    value: '1'
    description: Notification ID
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      const json = pm.response.json();
      pm.test('Notification is marked as read', () => { pm.expect(json.read).to.eql(true); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
""",

    r"Notifications\Mark All Notifications Read.request.yaml": """\
$kind: http-request
method: POST
url: '{{baseUrl}}/api/notifications/read-all'
order: 3000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
""",

    # ── Departments ───────────────────────────────────────────────────────────
    r"Departments\Get User Department.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/departments/user'
order: 1000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 200', () => { pm.response.to.have.status(200); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
      pm.test('Response has name', () => { pm.expect(json.name).to.be.a('string').and.not.empty; });
""",

    # ── Department Change Requests (user) ─────────────────────────────────────
    r"Department Change Requests\Get Pending Request.request.yaml": """\
$kind: http-request
method: GET
url: '{{baseUrl}}/api/department-change-requests/pending'
order: 1000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      pm.test('Status code is 200 or 404', () => {
        pm.expect(pm.response.code).to.be.oneOf([200, 404]);
      });
      if (pm.response.code === 200) {
        const json = pm.response.json();
        pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
        pm.test('Response has status', () => { pm.expect(json.status).to.be.a('string'); });
      }
""",

    r"Department Change Requests\Request Department Change.request.yaml": """\
$kind: http-request
method: POST
url: '{{baseUrl}}/api/department-change-requests'
order: 2000
auth:
  type: bearer
  credentials:
    - key: token
      value: '{{token}}'
headers:
  - key: Content-Type
    value: application/json
body:
  type: json
  content: |-
    {
      "requestedDepartmentId": 2
    }
scripts:
  - type: afterResponse
    language: text/javascript
    code: |-
      const json = pm.response.json();
      pm.test('Status code is 201', () => { pm.response.to.have.status(201); });
      pm.test('Response has id', () => { pm.expect(json.id).to.be.a('number'); });
      pm.test('Response has status PENDING', () => { pm.expect(json.status).to.eql('PENDING'); });
      pm.test('Response has requestedDepartment', () => { pm.expect(json.requestedDepartment).to.exist; });
""",
}

written = 0
for rel_path, content in files.items():
    full_path = os.path.join(BASE, rel_path)
    with open(full_path, "w", encoding="utf-8", newline="\n") as f:
        f.write(content)
    written += 1
    print(f"Written: {rel_path}")

print(f"\nDone. {written} files written.")
