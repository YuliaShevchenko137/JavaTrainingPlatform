CREATE SEQUENCE additional_number
 START WITH     1
 INCREMENT BY   1
 MAXVALUE 9999
 CYCLE;

CREATE OR REPLACE Function id_generator(curtime in number)
return number is genkey number;
begin
    genkey:=curtime*10000 + additional_number.NEXTVAL;
    RETURN genkey;
end;