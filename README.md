# OneManChallenge_BE
원맨 IT 뉴스 Backend Repo
- 30만건 이상의 IT 관련  뉴스를 빠르게 검색/조회하는 서비스

## 1. 기술스택
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white"> 

<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">

<img src="https://img.shields.io/badge/Elasticsearch-005571?style=for-the-badge&logo=Elasticsearch&logoColor=white"> <img src="https://img.shields.io/badge/Logstash-005571?style=for-the-badge&logo=Logstash&logoColor=white">

<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white"> 
<img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=Amazon EC2&logoColor=white">

<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
 

## 2. 구성도
![image](https://user-images.githubusercontent.com/31820402/224474232-894aeac9-32b1-4f04-9b33-a7b82e288fe9.png)

## 3. 대표기능 소개
### 3-1. Mysql Replication(이중화)
#### 구현 이유
1) 평소 구현해보고 싶었던 기능
2) 백업
> - 단일 DB서버의 경우 장애발생에 대한 우려
> - 유사시 복구 및 빠른 대응을 위한 준비의 필요성 인지
 3) 읽기와 쓰기를 따로 처리하는 ```CQRS패턴``` 적용

![image](https://user-images.githubusercontent.com/31820402/224474633-92732c26-2045-419b-9d74-f2a6b8c36ff6.png)

#### 느낀점
- 기능 구현 전에는 RDBMS의 data를 Disk I/O 처리하는 것으로 예상
- 실제 구현해보니 Binary log 내용 안의 sql문을 실행시키며 동기화 진행
- 과거 학습했던 Oracle Redo Log File과 흡사한 방식으로 진행되는 공통점 발견
- 이를 통해 Disk I/O와 ```Memory I/O```에 대한 인지와 성능 개선을 위해 ```Cache``` 사용 이유를 체감 
- 하지만 이런 장점을 악용한 ```Memory 해킹```에 대한 위협 인지

- - -
### 3-2. ElasticSearch를 활용한 검색속도 향상
#### 구현 이유
1) ```row data의 증가```에 따른 ```Mysql 조회 성능 저하``` 발생
2) 검색 성능 개선에서 항상 언급된 기능으로 도전욕구 자극
3) DB와 ElasticSearch 조회 성능을 비교 및 체감하고자 진행

![image](https://user-images.githubusercontent.com/31820402/224475727-81c98752-dc6f-43bc-9361-5045ae761a08.png)

- Logstash statement

```statement => "SELECT *, UNIX_TIMESTAMP(modification_date) AS unix_ts_in_secs FROM news WHERE (UNIX_TIMESTAMP(modification_date) > :sql_last_value AND modification_date < NOW()) ORDER BY modification_date ASC"```
#### 해석
- WHERE (UNIX_TIMESTAMP(modification_date) > :sql_last_value 
> 현재 저장된 수정일자 > 마지막 수정일자(:sql_last_value)
-  AND modification_date < NOW()) 
> 현재 저장된 수정일자 < 현재시간(NOW())
- 즉, ```현재 저장되어 있는 수정일자```를 참고하여
-  마지막 수정일자 < ```Logstash 수집 대상``` < 현재시간

#### 느낌점
- 성능 개선을 위한 다양한 ```Indexing``` 방법들이 있다는 것을 인지
- 키워드와 PK를 매핑시켜 빠른 검색을 가능하게 하는 ```역색인``` 기능 체감
- Logstach를 활용한 Mysql DB 동기화 과정을 통해 ```촘촘한 동기화```의 필요성 인지
- ```경로정보```를 ```주기적```으로 주고받아 ```빠른 경로```를 탐색하는 ```네트워크 라우팅 프로토콜```을 떠오르게 함

- 10초간 처리량 비교(ElasticSearch VS Mysql)
![image](https://user-images.githubusercontent.com/31820402/224504446-5e40d8d8-aad8-4adf-85ec-0a98f643476d.png)

- - -
### 3-3. Redis 토큰 관리
#### 구현 이유
1) Cache를 주제로 이야기할 때 항상 언급된 기능으로 도전욕구 자극
2) ```JWT 관리```에 적합한다고 판단
- 저장시간이 짧고, 자주 쓰이는 특징을 가진 data 처리에 적합
- 만료시간 설정 가능
3) DB의 부하를 줄이고, Cache의 유용함을 체감하고자 진행

![image](https://user-images.githubusercontent.com/31820402/224502979-410ac7c0-e5c5-4759-8cf5-d2c47de8815c.png)
![image](https://user-images.githubusercontent.com/31820402/224503049-25a58632-b369-441e-b8d5-3cf4af73ea93.png)

#### DB 토큰 관리가 아닌 Redis를 활용한 이점 5가지
1) 속도
> - Redis가 In-memory라서 빠름

2) 토큰 생명주기 관리 가능
> - Redis에서 세션처럼 관리되고 있는 key값들이 제거되면, 발급된 토큰을 재사용해도 Redis에 저장된 값이 없기 때문에 사용 불가(expireTime 활용)
> - DB로 구현할 경우 Client가 로그아웃 요청을 보내지 않을 경우 DB Table에 있는 이력을 삭제하기 위해서 스케쥴러 구현하여 DB 값 삭제해야 함 ==> 구현의 번거로움

3) RDBMS의 의존성 감소
> - 단일 책임 원칙(SRP)을 따름(하나의 메소드는 하나의 역할만 하도록 구현) 
> - 그래서 인증 검사를 Redis에게 역할 부여하고, RDBMS는 data 저장/관리 역할을 부여

4) data의 특성
> - RDBMS는 비교적 저장시간이 긴 data 저장 및 활용
> - Redis는 다른 data들에 비해 저장시간이 비교적이 짧고, 자주 쓰이는 data 저장 및 활용(캐시)

5) DB 쿼리 발생
> - DB Table에 토큰정보가 있다면 로그인, 인증, 로그아웃 할 때 SQL문이 발생 가능하며, DB서버로 트래픽 증가할 것으로 봄
> - 그래서 토큰 인증 관련한 캐시 역할을 하는 존재가 필요하다고 판단

#### 느낀점
- 영구적이지 않지만, 서비스 운영에 반드시 필요한 JWT 관리의 새로운 방법 습득
- 휘발성 data 처리를 위한 방법으로 Memory I/O 활용 인지
- 단순 빠른 처리가 아닌 ```의존도 감소```를 통한 성능 개선 경험(1기능 1역할, 부하 분산 등)
- - -
### 3-4. Node.js Single Thread를 활용한 동시성 제어
#### 구현 이유
1) Mysql Auto_increment 설정 후 ```Data 저장 실패 시```, Auto_increment 값이 ```Rollback 되지 않아``` 순서보장 안 되는 점 확인
![image](https://user-images.githubusercontent.com/31820402/224504145-3d015db0-c2ba-44b1-8014-bde94b9d3ead.png)
2) Redis 대신 Node.js의 ```Single Thread``` 특징을 활용하여 순서보장을 하고, 동시성 제어를 경험하고자 진행

![image](https://user-images.githubusercontent.com/31820402/224503859-7692a59f-63af-4406-9ac3-0f936579c0f9.png)
- node.js 코드

![image](https://user-images.githubusercontent.com/31820402/224504233-0059c4b9-fe29-4180-88ea-2bf202fb3b16.png)

#### 느낀점
- 동시성 제어를 위해 ```공간```이라는 특징을 활용
- 순서없이 동시 다발적으로 들어오는 요청을 ```병목현상```처럼 하나의 영역에서 순서대로 처리하는 방법을 체감
- ```공간```과 ```Single Thread```의 특징을 활용한 성능 개선을 경험
- ```대량의 패킷```들을 메시지 ```Queue```로 순서대로 처리하는 ```네트워크```와 비슷하여 신기했음
