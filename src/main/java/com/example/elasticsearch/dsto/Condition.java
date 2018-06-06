package com.example.elasticsearch.dsto;

import com.example.elasticsearch.utils.Page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author hell
 * @date 2018/4/16
 */
public class Condition {

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected Page page;

    public Condition() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void setOredCriteria(List<Criteria> oredCriteria) {
        this.oredCriteria = oredCriteria;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public static class Criteria{

        protected List<Criterion> criteria;

        protected Criteria() {
            criteria = new ArrayList<Criterion>();
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        public void setCriteria(List<Criterion> criteria) {
            this.criteria = criteria;
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public Criteria andEqualTo(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " = ",value));
            return this;
        }

        public Criteria andNotEqualTo(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " <> ",value));
            return this;
        }

        public Criteria andGreaterThan(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " > ",value));
            return this;
        }

        public Criteria andGreaterThanOrEqualTo(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field , " >= ",value));
            return this;
        }

        public Criteria andLessThan(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " < ",value));
            return this;
        }

        public Criteria andLessThanOrEqualTo(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " <= ",value));
            return this;
        }

        public Criteria andIsNull(String field) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " is null "));
            return this;
        }

        public Criteria andIsNotNull(String field) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field," is not null "));
            return this;
        }

        public Criteria andIn(String field,Iterable values) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " in ",values));
            return this;
        }

        public Criteria andNotIn(String field,Iterable values) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " not in ",values));
            return this;
        }

        public Criteria andBetween(String field,Object firstValue,Object secondValue) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " between " ,firstValue,secondValue));
            return this;
        }

        public Criteria andNotBetween(String field,Object firstValue,Object secondValue) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " not between " ,firstValue,secondValue));
            return this;
        }

        public Criteria andLike(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " like " ,value));
            return this;
        }

        public Criteria andNotLike(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field, " not like " ,value));
            return this;
        }
    }

    public static class Criterion {
        /**
         * 操作字段
         */
        private String field;
        /**
         * 操作符号
         */
        private String operation;
        /**
         * 查询的数据库操作和字段连接号
         */
        private String condition;
        /**
         * 查询的条件值
         */
        private Object value;
        /**
         * 第二个参数值（between语法）
         */
        private Object secondValue;
        /**
         * 没有值
         */
        private boolean noValue;
        /**
         * 单个值
         */
        private boolean singleValue;
        private boolean betweenValue;
        private boolean listValue;

        protected Criterion(String operation,String field) {
            this.field = field;
            this.operation = operation;
            this.condition = field + operation;
            this.noValue = true;
        }

        protected Criterion(String field,String operation,Object value) {
            this.field = field;
            this.operation = operation;
            this.condition = field + operation;
            this.value = value;
            if(value instanceof Collection<?>){
                this.listValue = true;
            }else{
                this.singleValue = true;
            }
        }

        protected Criterion(String field,String operation,Object firstValue, Object secondValue) {
            this.field = field;
            this.operation = operation;
            this.condition = field + operation;
            this.value = firstValue;
            this.secondValue = secondValue;
            this.betweenValue = true;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public void setSecondValue(Object secondValue) {
            this.secondValue = secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public void setNoValue(boolean noValue) {
            this.noValue = noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public void setSingleValue(boolean singleValue) {
            this.singleValue = singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public void setBetweenValue(boolean betweenValue) {
            this.betweenValue = betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public void setListValue(boolean listValue) {
            this.listValue = listValue;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
