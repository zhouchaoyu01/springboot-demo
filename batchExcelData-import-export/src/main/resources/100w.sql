CREATE TABLE `student_info` (
                                `id` INT ( 11 ) NOT NULL AUTO_INCREMENT,
                                `student_id` INT NOT NULL,
                                `name` VARCHAR ( 20 ) DEFAULT NULL,
                                `course_id` INT NOT NULL,
                                `class_id` INT ( 11 ) DEFAULT NULL,
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY ( `id` )
) ENGINE = INNODB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8;
CREATE TABLE `course` (
                          `id` INT ( 11 ) NOT NULL AUTO_INCREMENT,
                          `course_id` INT NOT NULL,
                          `course_name` VARCHAR ( 40 ) DEFAULT NULL,
                          PRIMARY KEY ( `id` )
) ENGINE = INNODB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8;

DELIMITER //
CREATE FUNCTION rand_string ( n INT ) RETURNS VARCHAR (255) #该函数会返回一个字符串
BEGIN
	DECLARE
chars_str VARCHAR ( 100 ) DEFAULT 'abcdefghijklmnopqrstuvwxyzABCDEFJHIJKLMNOPQRSTUVWXYZ';
	DECLARE
return_str VARCHAR ( 255 ) DEFAULT '';
	DECLARE
i INT DEFAULT 0;
	WHILE i < n DO
			SET return_str = CONCAT(return_str,SUBSTRING( chars_str, FLOOR(1+RAND()* 52), 1 ));
			SET i = i + 1;
END WHILE;
RETURN return_str;
END //
DELIMITER;


#函数2：创建随机数函数
DELIMITER //
CREATE FUNCTION rand_num (from_num INT ,to_num INT) RETURNS INT(11)
BEGIN
DECLARE i INT DEFAULT 0;
SET i = FLOOR(from_num +RAND()*(to_num - from_num+1)) ;
RETURN i;
END //
DELIMITER ;

show variables like 'log_bin_trust_function_creators';

set global log_bin_trust_function_creators=1;




# 存储过程1：创建插入课程表存储过程
DELIMITER //
CREATE PROCEDURE insert_course( max_num INT )
BEGIN
DECLARE i INT DEFAULT 0;
SET autocommit = 0; #设置手动提交事务
REPEAT #循环
SET i = i + 1; #赋值
INSERT INTO course (course_id, course_name ) VALUES
(rand_num(10000,10100),rand_string(6));
UNTIL i = max_num END REPEAT;
COMMIT; #提交事务
END //
DELIMITER ;

# 存储过程2：创建插入学生信息表存储过程
DELIMITER //
CREATE PROCEDURE insert_stu( max_num INT )
BEGIN
DECLARE i INT DEFAULT 0;
SET autocommit = 0; #设置手动提交事务
REPEAT #循环
SET i = i + 1; #赋值
INSERT INTO student_info (course_id, class_id ,student_id ,NAME ) VALUES
(rand_num(10000,10100),rand_num(10000,10200),rand_num(1,200000),rand_string(6));
UNTIL i = max_num
END REPEAT;
COMMIT; #提交事务
END //
DELIMITER ;


CALL insert_course(100); -- 0.038
CALL insert_stu(1000000); -- 341.03s