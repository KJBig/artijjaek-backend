# Subscription Top API

관리자 페이지에서 구독자들이 많이 구독한 회사/카테고리를 조회하는 API입니다.

## 공통
- 인증: 관리자 인증 필요
- 응답 래퍼: `SuccessDataResponse`
- Top 기준: `5위`
- 동점 정책: `5위 구독 수와 동일하면 모두 포함` (결과 건수가 5개를 초과할 수 있음)
- 정렬: `subscriberCount DESC` 후 이름 오름차순(동률 안정 정렬)

## 1) 많이 구독한 회사 조회
- Method: `GET`
- Path: `/admin/v1/company/subscribed/top`

### Request Example
```http
GET /admin/v1/company/subscribed/top
```

### Response Example
```json
{
  "isSuccess": true,
  "message": "요청성공",
  "data": [
    {
      "rank": 1,
      "companyId": 10,
      "companyNameKr": "회사A",
      "subscriberCount": 120
    },
    {
      "rank": 2,
      "companyId": 11,
      "companyNameKr": "회사B",
      "subscriberCount": 95
    },
    {
      "rank": 2,
      "companyId": 12,
      "companyNameKr": "회사C",
      "subscriberCount": 95
    }
  ]
}
```

### Response Fields
- `rank`: dense rank (동일 구독 수는 동일 순위)
- `companyId`: 회사 ID
- `companyNameKr`: 회사명
- `subscriberCount`: 구독자 수

## 2) 많이 구독한 카테고리 조회
- Method: `GET`
- Path: `/admin/v1/category/subscribed/top`

### Request Example
```http
GET /admin/v1/category/subscribed/top
```

### Response Example
```json
{
  "isSuccess": true,
  "message": "요청성공",
  "data": [
    {
      "rank": 1,
      "categoryId": 20,
      "categoryName": "백엔드",
      "subscriberCount": 140
    },
    {
      "rank": 2,
      "categoryId": 21,
      "categoryName": "프론트",
      "subscriberCount": 110
    }
  ]
}
```

### Response Fields
- `rank`: dense rank (동일 구독 수는 동일 순위)
- `categoryId`: 카테고리 ID
- `categoryName`: 카테고리명
- `subscriberCount`: 구독자 수
