# Admin Member List API

## Endpoint
- `GET /admin/v1/member/list`

## Auth
- Required
- Header: `Authorization: Bearer <accessToken>`

## Query Parameters
- `page` (`number`, optional): page index (0-base)
- `size` (`number`, optional): page size
- `status` (`string`, optional, default: `ALL`)
  - `ALL` | `ACTIVE` | `DELETED`
- `searchType` (`string`, optional)
  - `NICKNAME` | `EMAIL`
- `keyword` (`string`, optional)
  - empty/blank string is treated as no keyword
- `sortBy` (`string`, optional, default: `SUBSCRIBE_DATE`)
  - `SUBSCRIBE_DATE` | `NICKNAME` | `EMAIL` | `STATUS`
- `sortDirection` (`string`, optional, default: `DESC`)
  - `ASC` | `DESC`

## Sort Mapping (server internal)
- `SUBSCRIBE_DATE` -> createdAt
- `NICKNAME` -> nickname
- `EMAIL` -> email
- `STATUS` -> memberStatus
- all sort options use `id` as secondary sort key

## Success Response
- HTTP `200 OK`

```json
{
  "isSuccess": true,
  "message": "요청성공",
  "data": {
    "pageNumber": 0,
    "totalCount": 15,
    "hasNext": true,
    "content": [
      {
        "memberId": 1,
        "email": "john.doe@example.com",
        "nickname": "John Doe",
        "memberStatus": "ACTIVE",
        "createdAt": "2024-12-15T00:00:00"
      }
    ],
    "statusCount": {
      "allCount": 15,
      "activeCount": 11,
      "deletedCount": 4
    }
  }
}
```

## Field Definitions
- `data.pageNumber`: current page number (0-base)
- `data.totalCount`: total member count for current filter/search condition
- `data.hasNext`: whether next page exists
- `data.content[]`: current page member list
- `data.statusCount`: status-based total counts (all/active/deleted)

## Member Status Enum
- `ACTIVE`
- `DELETED`

## Error Response Format
- Authentication/authorization/token errors and other application errors use:

```json
{
  "isSuccess": false,
  "code": "REE-2",
  "message": "토큰이 존재하지 않습니다."
}
```

## Frontend TypeScript Types (recommended)

```ts
export type MemberStatusFilter = "ALL" | "ACTIVE" | "DELETED";
export type MemberListSearchType = "NICKNAME" | "EMAIL";
export type MemberListSortBy = "SUBSCRIBE_DATE" | "NICKNAME" | "EMAIL" | "STATUS";
export type SortDirection = "ASC" | "DESC";
export type MemberStatus = "ACTIVE" | "DELETED";

export interface MemberSimpleResponse {
  memberId: number;
  email: string;
  nickname: string;
  memberStatus: MemberStatus;
  createdAt: string; // ISO-8601
}

export interface MemberStatusCountResponse {
  allCount: number;
  activeCount: number;
  deletedCount: number;
}

export interface MemberListPageResponse {
  pageNumber: number;
  totalCount: number;
  hasNext: boolean;
  content: MemberSimpleResponse[];
  statusCount: MemberStatusCountResponse;
}

export interface SuccessDataResponse<T> {
  isSuccess: boolean;
  message: string;
  data: T;
}
```

## Request Example

```bash
curl -G 'https://{host}/admin/v1/member/list' \
  -H 'Authorization: Bearer {accessToken}' \
  --data-urlencode 'page=0' \
  --data-urlencode 'size=20' \
  --data-urlencode 'status=ALL' \
  --data-urlencode 'searchType=NICKNAME' \
  --data-urlencode 'keyword=john' \
  --data-urlencode 'sortBy=SUBSCRIBE_DATE' \
  --data-urlencode 'sortDirection=DESC'
```
