# exercise_metric_requirements

## 1. 테이블 개요 (Table Overview)
| 항목 | 내용 |
| :--- | :--- |
| **테이블 명** | `exercise_metric_requirements` |
| **설명** | 각 **운동(`exercises`)**과 **1:1**로 매핑되어, 해당 운동에서 기록 가능한 핵심 메트릭(무게, 횟수, 거리, 시간)의 **상태**를 `FORBIDDEN/OPTIONAL/REQUIRED` 3단계로 관리합니다. |


## 2. 타입 정의 (Types)
- **`exercise_metric_requirements` (ENUM)**  
  - 값: `FORBIDDEN`(불가), `OPTIONAL`(선택), `REQUIRED`(필수)  
  - 목적: 메트릭 입력의 요구 수준을 단일 컬럼로 명확히 표현


## 3. 컬럼 명세 (Column Specification)
- `exercise_id` **BIGINT**: PK이자 FK. `exercises.id` 참조, **1:1** 매핑 보장. 운동이 삭제되면 해당 행도 **CASCADE**로 삭제.
- `weight_kg_status` **exercise_metric_requirements NOT NULL DEFAULT 'FORBIDDEN'**: 무게(kg) 입력 상태.
- `reps_status` **exercise_metric_requirements NOT NULL DEFAULT 'FORBIDDEN'**: 반복(회) 입력 상태.
- `distance_m_status` **exercise_metric_requirements NOT NULL DEFAULT 'FORBIDDEN'**: 거리(m) 입력 상태.
- `duration_sec_status` **exercise_metric_requirements NOT NULL DEFAULT 'FORBIDDEN'**: 시간(초) 입력 상태.

> 모든 상태 컬럼은 `NOT NULL`이며 기본값은 `FORBIDDEN`입니다.


## 4. 관계 (Relations)
- **`exercises` (1:1)**  
  - `exercise_metric_flags.exercise_id` → `exercises.id`  
  - `ON DELETE CASCADE`: 운동 삭제 시 플래그 행도 함께 삭제


## 5. 인덱스 (Indexes)
| 인덱스 명 | 컬럼 | 설명 |
| :--- | :--- | :--- |
| `exercise_metric_requirements` | `(exercise_id)` | 기본 키(= 외래 키). 1:1 매핑 보장 |

> 별도의 보조 인덱스는 필요하지 않습니다(조회 키가 PK이므로).


## 6. DDL 쿼리문 (DDL Query)
```sql
-- 1) 상태 타입 정의: 3단계 고정
DO $$ BEGIN
    CREATE TYPE metric_requirement AS ENUM ('FORBIDDEN', 'OPTIONAL', 'REQUIRED');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

-- 2) 운동별 핵심 메트릭 상태 테이블 (1:1)
DROP TABLE IF EXISTS exercise_metric_requirements CASCADE;

CREATE TABLE exercise_metric_requirements (
  exercise_id BIGINT PRIMARY KEY REFERENCES exercises(id) ON DELETE CASCADE,

  weight_kg_status    metric_requirement NOT NULL DEFAULT 'FORBIDDEN',
  reps_status         metric_requirement NOT NULL DEFAULT 'FORBIDDEN',
  distance_m_status   metric_requirement NOT NULL DEFAULT 'FORBIDDEN',
  duration_sec_status metric_requirement NOT NULL DEFAULT 'FORBIDDEN'
);

COMMENT ON TABLE exercise_metric_requirements IS '운동별 핵심 메트릭(무게/횟수/거리/시간)의 입력 상태(FORBIDDEN/OPTIONAL/REQUIRED)';

```