# 예제 - 음식배달

본 예제는 MSA/DDD/Event Storming/EDA 를 포괄하는 분석/설계/구현/운영 전단계를 커버하도록 구성한 예제입니다.
이는 클라우드 네이티브 애플리케이션의 개발에 요구되는 체크포인트들을 통과하기 위한 예시 답안을 포함합니다.

# 서비스 시나리오

기능적 요구사항
1. 고객이 메뉴를 선택하여 주문한다.
1. 고객이 선택한 메뉴에 대해 결제한다.
1. 주문이 되면 주문 내역이 입점상점주인에게 주문정보가 전달된다
1. 상점주는 주문을 수락하거나 거절할 수 있다
1. 상점주는 요리시작때와 완료 시점에 시스템에 상태를 입력한다
1. 고객은 아직 요리가 시작되지 않은 주문은 취소할 수 있다
1. 요리가 완료되면 고객의 지역 인근의 라이더들에 의해 배송건 조회가 가능하다
1. 라이더가 해당 요리를 Pick한 후, 앱을 통해 통보한다.
1. 고객이 주문상태를 중간중간 조회한다
1. 라이더의 배달이 끝나면 배송확인 버튼으로 모든 거래가 완료된다.


비기능적 요구사항
1. 장애격리
    1. 상점관리 기능이 수행되지 않더라도 주문은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    1. 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
1. 성능
    1. 고객이 자주 상점관리에서 확인할 수 있는 배달상태를 주문시스템(프론트엔드)에서 확인할 수 있어야 한다  CQRS
    1. 배달상태가 바뀔때마다 카톡 등으로 알림을 줄 수 있어야 한다  Event driven


## Model
![이벤트스토밍](https://user-images.githubusercontent.com/53729857/205811898-fe557cc1-6bc2-4dbf-867c-1734bb86d370.png)


요구사항을 커버하는지 검증

![image](https://user-images.githubusercontent.com/53729857/205814704-19cd22d1-88d2-4860-bc4c-5ad7ab0b02ee.png)
    
    - 고객이 메뉴를 선택하여 주문한다. (ok)
    - 고객이 선택한 메뉴에 대해 결제한다. (ok)
    - 주문이 되면 주문 내역이 입점상점주인에게 주문정보가 전달된다 (ok)
    
![image](https://user-images.githubusercontent.com/53729857/205815658-a7e27991-af7b-44a4-9c76-d732b456a40a.png)

    - 상점주는 주문을 수락하거나 거절할 수 있다 (ok)
    - 상점주는 요리시작때와 완료 시점에 시스템에 상태를 입력한다 (ok)
    - 요리가 완료되면 고객의 지역 인근의 라이더들에 의해 배송건 조회가 가능하다 (ok)
    - 주문상태가 바뀔 때 마다 카톡으로 알림을 보낸다 (ok)
    
![image](https://user-images.githubusercontent.com/53729857/205816589-5c4d93b4-0503-46f9-98bf-d08db232451b.png)

    - 라이더가 해당 요리를 Pick한 후, 앱을 통해 통보한다. (ok)
    
![image](https://user-images.githubusercontent.com/53729857/205816875-45de8307-24b5-4cd8-a969-d7189fb85c7a.png) 
    
    - 고객은 아직 요리가 시작되지 않은 주문은 취소할 수 있다 (ok)
    - 고객이 주문상태를 중간중간 조회한다 (ok)
    - 고객이 요리를 배달 받으면 배송확인 버튼을 탭하여, 모든 거래가 완료된다 (ok)
  

# 체크포인트
# Microservice Implementation
## 1. Saga(Pub/Sub)
![saga](https://user-images.githubusercontent.com/85158266/219257066-f832a676-5e77-4341-8ede-4ae036f35576.JPG)

orders로 post 요청을 보내면 OrderPlaced에서 pay에 있는 pay커맨드로 요청을 전달한다.(req/res : 동기)
그 후에 pay에서 PaymentApproved이벤트를 거쳐 store에 있는 receipt정책으로 이벤트를 전달한다.(Pub/Sub : 비동기)
아래는 orders post요청으로 3개의 테이블에 데이터가 들어간 것을 확인한 증적이다.

```
gitpod /workspace/mall (main) $ http POST http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders item="치킨" qty=10 price=200 state="주문접수-결재완료"
HTTP/1.1 201 Created
Content-Type: application/json
Date: Thu, 16 Feb 2023 03:04:53 GMT
Location: http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
transfer-encoding: chunked

{
    "_links": {
        "order": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        }
    },
    "item": "치킨",
    "price": 200,
    "qty": 10,
    "state": "주문접수-결재완료"
}


gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/payments
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/hal+json
Date: Tue, 06 Dec 2022 06:19:36 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_embedded": {
        "payments": [
            {
                "_links": {
                    "payment": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/payments/1"
                    },
                    "self": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/payments/1"
                    }
                },
                "action": "progress",
                "amount": 2000,
                "orderId": 1
            }
        ]
    },
    "_links": {
        "profile": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/profile/payments"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/payments"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 1,
        "totalPages": 1
    }
}


gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderManagements
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/hal+json
Date: Tue, 06 Dec 2022 06:19:51 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_embedded": {
        "orderManagements": [
            {
                "_links": {
                    "orderManagement": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderManagements/1"
                    },
                    "self": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderManagements/1"
                    }
                },
                "address": "test주소",
                "foodType": "한식",
                "orderId": 1,
                "state": null
            }
        ]
    },
    "_links": {
        "profile": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderManagements"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderManagements"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 1,
        "totalPages": 1
    }
}


gitpod /workspace/mall (main) $ 
```

## 2. CQRS 
![cqrs](https://user-images.githubusercontent.com/85158266/219261446-c99c0335-b11c-4373-9057-84fa5dbf764e.JPG)
읽기 모델을 분리한다.
- app -> OrderStateViewHandler.java에서 이벤트에 따라 Real Model 저장, 업데이트, 삭제를 정의한다. 
```
package mall.infra;

import mall.domain.*;
import mall.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderStateViewHandler {

    @Autowired
    private OrderStateRepository orderStateRepository;

    // 생성될 경우 해당 Real Model에 값 저장
    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderPlaced_then_CREATE_1 (@Payload OrderPlaced orderPlaced) {
        try {

            if (!orderPlaced.validate()) return;

            // view 객체 생성
            OrderState orderState = new OrderState();
            // view 객체에 이벤트의 Value 를 set 함
            orderState.setId(orderPlaced.getId());
            orderState.setItem(orderPlaced.getItem());
            orderState.setState("생성");
            // view 레파지 토리에 save
            orderStateRepository.save(orderState);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

- 아래의 결과처럼 orderStates에 저장된다.
```
gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/hal+json
Date: Tue, 06 Dec 2022 08:22:59 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_embedded": {
        "orderStates": []
    },
    "_links": {
        "profile": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/profile/orderStates"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 0,
        "totalPages": 0
    }
}


gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders item="치킨" qty=10 price=200 state="주문접수-결재완료"
HTTP/1.1 201 
Connection: keep-alive
Content-Type: application/json
Date: Tue, 06 Dec 2022 08:23:10 GMT
Keep-Alive: timeout=60
Location: http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_links": {
        "order": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        }
    },
    "item": "치킨",
    "price": 200,
    "qty": 10,
    "state": "주문접수-결재완료"
}


gitpod /workspace/mall (main) $ http :8081/orderStates
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/hal+json
Date: Tue, 06 Dec 2022 08:23:23 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_embedded": {
        "orderStates": [
            {
                "_links": {
                    "orderState": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates/1"
                    },
                    "self": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates/1"
                    }
                },
                "item": "치킨",
                "state": "생성"
            }
        ]
    },
    "_links": {
        "profile": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/profile/orderStates"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 1,
        "totalPages": 1
    }
}


gitpod /workspace/mall (main) $ 
```


## 2-1. CQRS 
- 수정된 모델 -> 단독 컨텍스트에 구현한 뒤 OrderPlaced 이벤트 발생과 OrderCanceled 이벤트 발생에 따른 리얼 모델의 변경 확인
![image](https://user-images.githubusercontent.com/53729857/206223760-f2044d57-d801-41c6-8d1d-a01d053cf03d.png)

```
gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders item="치킨" qty=10 price=200 state="주문접수-결재완료"
HTTP/1.1 201 
Connection: keep-alive
Content-Type: application/json
Date: Wed, 07 Dec 2022 15:36:05 GMT
Keep-Alive: timeout=60
Location: http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_links": {
        "order": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        }
    },
    "item": "치킨",
    "price": 200,
    "qty": 10,
    "state": "주문접수-결재완료"
}


gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/hal+json
Date: Wed, 07 Dec 2022 15:36:15 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_embedded": {
        "orderStates": [
            {
                "_links": {
                    "orderState": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates/1"
                    },
                    "self": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates/1"
                    }
                },
                "item": "치킨",
                "orderState": "시작",
                "payState": null
            }
        ]
    },
    "_links": {
        "profile": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/profile/orderStates"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 1,
        "totalPages": 1
    }
}


gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders id=1 state="주문취소"
HTTP/1.1 201 
Connection: keep-alive
Content-Type: application/json
Date: Wed, 07 Dec 2022 15:36:22 GMT
Keep-Alive: timeout=60
Location: http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_links": {
        "order": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        }
    },
    "item": null,
    "price": null,
    "qty": null,
    "state": "주문취소"
}


gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/hal+json
Date: Wed, 07 Dec 2022 15:36:49 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_embedded": {
        "orderStates": [
            {
                "_links": {
                    "orderState": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates/1"
                    },
                    "self": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates/1"
                    }
                },
                "item": "치킨",
                "orderState": "시작",
                "payState": "결제취소"
            }
        ]
    },
    "_links": {
        "profile": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/profile/orderStates"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orderStates"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 1,
        "totalPages": 1
    }
}

gitpod /workspace/mall (main) $ 
```

## 3. Compensation / Correlation
- 보상안과 그에대한 처리 -> OrderCanceled를 실행할 시 pay cancel 정책을 통해 결제내역을 삭제한다.

state가 주문취소일시 OrderCanceled 이벤트 pub

![image](https://user-images.githubusercontent.com/53729857/206229269-54ba5dff-1967-4a27-97e3-fc3f4801a893.png)

OrderCanceled를 수신하는 pay쪽 PolicyHandler -> payCancel실행

![image](https://user-images.githubusercontent.com/53729857/206231803-26fd6eff-6107-428d-9046-00f8c97530a6.png)

orderId 기반으로 Payment를 찾아 삭제 -> 그전에 PaymentRepository - findByOrderId(findBy컬럼명)으로 메소드 생성 필요

![image](https://user-images.githubusercontent.com/53729857/206232727-7f86bc5e-59a3-42d3-89ed-6f6928a9c010.png)

![image](https://user-images.githubusercontent.com/53729857/206232766-928b121f-ad14-4805-be5b-a8e54fe4f597.png)


결과 -> order 업데이트, pay 삭제
```
gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/ item="치킨" qty=10 price=200 state="주문접수-결재완료"
HTTP/1.1 201 
Connection: keep-alive
Content-Type: application/json
Date: Wed, 07 Dec 2022 16:14:32 GMT
Keep-Alive: timeout=60
Location: http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_links": {
        "order": {
            "href": "http://http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        }
    },
    "item": "치킨",
    "price": 200,
    "qty": 10,
    "state": "주문접수-결재완료"
}


gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/hal+json
Date: Wed, 07 Dec 2022 16:14:38 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_embedded": {
        "orders": [
            {
                "_links": {
                    "order": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
                    },
                    "self": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
                    }
                },
                "item": "치킨",
                "price": 200,
                "qty": 10,
                "state": "주문접수-결재완료"
            }
        ]
    },
    "_links": {
        "profile": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/profile/orders"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 1,
        "totalPages": 1
    }
}


gitpod /workspace/mall (main) $ http :8084/payments
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/hal+json
Date: Wed, 07 Dec 2022 16:14:58 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_embedded": {
        "payments": [
            {
                "_links": {
                    "payment": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/payments/1"
                    },
                    "self": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/payments/1"
                    }
                },
                "action": "progress",
                "amount": 2000,
                "orderId": 1
            }
        ]
    },
    "_links": {
        "profile": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/profile/payments"
        },
        "search": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/payments/search"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/payments"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 1,
        "totalPages": 1
    }
}


gitpod /workspace/mall (main) $ http :8081/orders id=1 state="주문취소"
HTTP/1.1 201 
Connection: keep-alive
Content-Type: application/json
Date: Wed, 07 Dec 2022 16:15:57 GMT
Keep-Alive: timeout=60
Location: http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_links": {
        "order": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
        }
    },
    "item": null,
    "price": null,
    "qty": null,
    "state": "주문취소"
}


gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/hal+json
Date: Wed, 07 Dec 2022 16:16:05 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_embedded": {
        "orders": [
            {
                "_links": {
                    "order": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
                    },
                    "self": {
                        "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders/1"
                    }
                },
                "item": null,
                "price": null,
                "qty": null,
                "state": "주문취소"
            }
        ]
    },
    "_links": {
        "profile": {
            "href": "http://http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/profile/orders"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/orders"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 1,
        "totalPages": 1
    }
}


gitpod /workspace/mall (main) $ http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/payments
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/hal+json
Date: Wed, 07 Dec 2022 16:16:19 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_embedded": {
        "payments": []
    },
    "_links": {
        "profile": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/profile/payments"
        },
        "search": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/payments/search"
        },
        "self": {
            "href": "http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/payments"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 0,
        "totalPages": 0
    }
}


gitpod /workspace/mall (main) $ 
```

# Microservice Orchestration

## 1. Deploy to EKS Cluster
![deploy](https://user-images.githubusercontent.com/85158266/219265698-179cf74f-d242-4bff-9d80-7faacdb45956.JPG)

ESK Cluster 내에서 kubectl get all 명령으로 조회


## 2. Gateway Service Router 설치
![gateway](https://user-images.githubusercontent.com/85158266/219266042-3f6567bc-f507-40c9-8b4b-3adc053ff39d.JPG)

service 목록에 외부 노출 LoadBalancer 타입의 gateway 배포


## 3. Autoscale (HPA)

siege pod 내에서 부하를 발생시켜 Autoscale동작을 확인
siege -c20 -t40S -v http://customer:8080/customer 명령으로 부하를 발생시킨다.

![before_siege_pod](https://user-images.githubusercontent.com/85158266/219268519-dd52208f-1d08-4b92-a0be-48cdfac77e79.JPG)

부하 발생 전 pod 갯수


![before_siege_hpa](https://user-images.githubusercontent.com/85158266/219268539-d843fe22-bc39-41e3-b87d-3b48218acdc7.JPG)

부하 발생 전 hpa


![siege_pod](https://user-images.githubusercontent.com/85158266/219268555-956d5f8b-a235-4eb7-9dec-de94c6711f82.JPG)

부하 발생 직 후 pod 생성


![after_siege_pod_hpa](https://user-images.githubusercontent.com/85158266/219268563-5a1f1edd-d6a0-420c-9b20-ccf51ee9cbf4.JPG)

부하 발생 후 pod 과 hpa 상태



# 그외
## 1. Request / Response

![image](https://user-images.githubusercontent.com/53729857/205790895-04938551-3ad8-471c-b8c6-676527a106a7.png)

Reqeust / Response : Pay쪽 서버가 올라오지 않을경우 에러발생.


![image](https://user-images.githubusercontent.com/53729857/205791167-d9b4359b-e816-482a-90bc-3ba348ab5a67.png)

Reqeust / Response : Pay쪽 서버가 정상일 경우만 정상 작동

Pub / Sub : Store쪽 서버가 올라오지 않을 경우에도 올라온 서버안에서 정상작동


## 2. Circuit Breaker
- pay -> Payment.java에 추가
```
    @PrePersist
    public void onPrePersist(){
        if(action.equals("canceled")){
            PaymentCanceled paymentCanceled = new PaymentCanceled();
            BeanUtils.copyProperties(this, paymentCanceled);
            paymentCanceled.publish();
        }else if(action.equals("progress")){
            PaymentApproved paymentApproved = new PaymentApproved();
            BeanUtils.copyProperties(this, paymentApproved);

            // 주문 정보가 커밋된 후에 이벤트 발생시켜야 한다.
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    paymentApproved.publish();
                }
            });
            
            // 강제 지연
            try {
                Thread.currentThread().sleep((long) (1000 + Math.random() * 220));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else{
            System.out.println("알 수 없는 action");
        }

    }
```
- app -> pom.xml에 추가
```
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
		</dependency>
```
- app -> application.yml에 추가
```
feign:
  hystrix:
    enabled: true

hystrix:
  command:
    # 전역설정
    default:
      execution.isolation.thread.timeoutInMilliseconds: 10
```
- app -> PaymentServiceFallBack.java 
```
package mall.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
public class PaymentServiceFallBack implements PaymentService {
    private static Logger logger = LoggerFactory.getLogger(PaymentServiceFallBack.class);

    @Override
    public void pay(Payment payment) {
        logger.error("Circuit breaker has been opened. Fallback returned instead.");
    }
}
```
- 실행 시 아래의 에러 로그를 app쪽 로그에서 확인가능
![image](https://user-images.githubusercontent.com/53729857/205817063-abf4008f-a96b-4f6f-8c28-b493878baf36.png)

- 과부하 테스트 명령어
![image](https://user-images.githubusercontent.com/53729857/205828449-8838f7cb-3342-48d6-b1e2-de4d36ccc191.png)

- 초기상태
![image](https://user-images.githubusercontent.com/53729857/205828846-d13262ad-8136-46d6-be86-53db3531fde9.png)

- 요청이 점점 밀리는 것을 확인 가능
![image](https://user-images.githubusercontent.com/53729857/205829022-646faa3b-1707-4a8e-9987-85f6d08a9cd3.png)

- 종료
![image](https://user-images.githubusercontent.com/53729857/205829348-11600e6b-6307-4586-9939-695e9e5f56d3.png)

- WAS 로그 - 중간중간 FallBack 로그가 있음
![image](https://user-images.githubusercontent.com/53729857/205829830-ea1edeac-a025-41fe-9d50-92d15ed502d7.png)

## 3. Gateway / Ingress
gateway의 라우터 설정으로 http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/ 으로 각 서비스로 라우팅 서비스를 제공한다.
```
spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: app
          uri: http://a9be8acfa40bf45dd861a2cd4b56fddd-1444298964.ap-northeast-2.elb.amazonaws.com:8080/
          predicates:
            - Path=/orders/**, /menus/**, /orderStates/**
```
![image](https://user-images.githubusercontent.com/53729857/205836882-b940a8ac-e567-43c0-b569-4b6adc0aa981.png)
![image](https://user-images.githubusercontent.com/53729857/205836894-5858d3c0-08d5-4bd7-b8b0-7dba84c23b8a.png)


# 기타
## Before Running Services
### Make sure there is a Kafka server running
```
cd kafka
docker-compose up
```
- Check the Kafka messages:
```
cd kafka
docker-compose exec -it kafka /bin/bash
cd /bin
./kafka-console-consumer --bootstrap-server localhost:9092 --topic
```

## Run the backend micro-services
See the README.md files inside the each microservices directory:

- app
- store
- customer
- pay
- rider


## Run API Gateway (Spring Gateway)
```
cd gateway
mvn spring-boot:run
```

## Test by API
- app
```
 http :8088/orders id="id" item="item" qty="qty" price="price" state="state" 
```
- store
```
 http :8088/orderManagements id="id" orderId="orderId" address="address" foodType="foodType" state="state" 
```
- customer
```
```
- pay
```
 http :8088/payments id="id" orderId="orderId" amount="amount" action="action" 
```
- rider
```
 http :8088/deliveries id="id" orderId="orderId" state="state" address="address" 
```


## Run the frontend
```
cd frontend
npm i
npm run serve
```

## Test by UI
Open a browser to localhost:8088

## Required Utilities

- httpie (alternative for curl / POSTMAN) and network utils
```
sudo apt-get update
sudo apt-get install net-tools
sudo apt install iputils-ping
pip install httpie
```

- kubernetes utilities (kubectl)
```
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

- aws cli (aws)
```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

- eksctl 
```
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
```

