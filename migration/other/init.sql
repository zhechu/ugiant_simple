--	����
drop database if exists flywaydemo ;
create database	flywaydemo default charset utf8;

--	�����û�
create user 'flywaydemo'@'localhost' identified by 'flywaydemo';


--	����Ȩ��
grant all privileges on flywaydemo.* to 'flywaydemo'@'localhost' with grant option;

grant super on *.* to 'flywaydemo'@'localhost' with grant option;

flush privileges; 