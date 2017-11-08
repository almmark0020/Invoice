ALTER TABLE COMPANIAS ADD RFC CHAR(13)  null;
ALTER TABLE COMPANIAS ADD RAZON_SOCIAL CHAR(100) null;
ALTER TABLE COMPANIAS ADD REGIMEN_FISCAL CHAR(3) null;
ALTER TABLE COMPANIAS ADD LUGAR_EXPEDICION_CP CHAR(5) null;
 
update COMPANIAS set RAZON_SOCIAL = 'ABA SEGUROS, S.A. DE C.V.', RFC = 'ABA920310QW0', LUGAR_EXPEDICION_CP = '66260', REGIMEN_FISCAL = '601' WHERE CIA_ID = 2;
update COMPANIAS set RAZON_SOCIAL = 'SEGUROS EL POTOSI, S.A.', RFC = 'SPO830427DQ1', LUGAR_EXPEDICION_CP = '78000', REGIMEN_FISCAL = '601' WHERE CIA_ID = 8;
update COMPANIAS set RAZON_SOCIAL = 'GENERAL DE SEGUROS, S.A.B.', RFC = 'GSE720216JJ6', LUGAR_EXPEDICION_CP = '03800', REGIMEN_FISCAL = '601' WHERE CIA_ID = 9;
update COMPANIAS set RAZON_SOCIAL = 'SEGUROS SURA,S.A. DE C.V.', RFC = 'R&S811221KR6', LUGAR_EXPEDICION_CP = '01060', REGIMEN_FISCAL = '601' WHERE CIA_ID = 10;
update COMPANIAS set RAZON_SOCIAL = 'AIG SEGUROS MEXICO, S.A. DE C.V.', RFC = 'CSM960528CC4', LUGAR_EXPEDICION_CP = '03219', REGIMEN_FISCAL = '601' WHERE CIA_ID = 12;
update COMPANIAS set RAZON_SOCIAL = 'LA LATINOAMERICANA SEGUROS, S.A.', RFC = 'LSE7406056F6', LUGAR_EXPEDICION_CP = '06007', REGIMEN_FISCAL = '601' WHERE CIA_ID = 13;
update COMPANIAS set RAZON_SOCIAL = 'SEGUROS VE POR MAS, S.A. GRUPO FINANCIERO VE POR MAS', RFC = 'SMS401001573', LUGAR_EXPEDICION_CP = '06500', REGIMEN_FISCAL = '601' WHERE CIA_ID = 14;
update COMPANIAS set RAZON_SOCIAL = 'SEGUROS INBURSA,S.A. GRUPO FINANCIERO INBURSA', RFC = 'SIN9408027L7', LUGAR_EXPEDICION_CP = '14060', REGIMEN_FISCAL = '601' WHERE CIA_ID = 16;
update COMPANIAS set RAZON_SOCIAL = 'SEGUROS ATLAS, S.A.', RFC = 'SAT8410245V8', LUGAR_EXPEDICION_CP = '05120', REGIMEN_FISCAL = '601' WHERE CIA_ID = 17;
update COMPANIAS set RAZON_SOCIAL = 'ZURICH COMPAÑIA DE SEGUROS, S.A.', RFC = 'ZSE950306M48', LUGAR_EXPEDICION_CP = '11520', REGIMEN_FISCAL = '601' WHERE CIA_ID = 19;
update COMPANIAS set RAZON_SOCIAL = 'HDI SEGUROS, S.A. DE C.V.', RFC = 'HSE701218532', LUGAR_EXPEDICION_CP = '37306', REGIMEN_FISCAL = '601' WHERE CIA_ID = 21;
update COMPANIAS set RAZON_SOCIAL = 'ACE SEGUROS, S.A.', RFC = 'ASE901221SM4', LUGAR_EXPEDICION_CP = '05120', REGIMEN_FISCAL = '601' WHERE CIA_ID = 27;
update COMPANIAS set RAZON_SOCIAL = 'MAPFRE TEPEYAC, S.A', RFC = 'MTE440316E54', LUGAR_EXPEDICION_CP = '06500', REGIMEN_FISCAL = '601' WHERE CIA_ID = 29;
update COMPANIAS set RAZON_SOCIAL = 'SEGUROS BANORTE, S.A. DE C.V. GRUPO FINANCIERO BANORTE', RFC = 'SBG971124PL2', LUGAR_EXPEDICION_CP = '64000', REGIMEN_FISCAL = '601' WHERE CIA_ID = 30;
update COMPANIAS set RAZON_SOCIAL = 'GRUPO NACIONAL PROVINCIAL, S.A.B', RFC = 'GNP9211244P0', LUGAR_EXPEDICION_CP = '04200', REGIMEN_FISCAL = '601' WHERE CIA_ID = 31;
update COMPANIAS set RAZON_SOCIAL = 'QUALITAS COMPAÑIA DE SEGUROS, S.A. DE C.V.', RFC = 'QCS931209G49', LUGAR_EXPEDICION_CP = '05200', REGIMEN_FISCAL = '601' WHERE CIA_ID = 34;
update COMPANIAS set RAZON_SOCIAL = 'AXA SEGUROS, S.A. DE C.V.', RFC = 'ASE931116231', LUGAR_EXPEDICION_CP = '03200', REGIMEN_FISCAL = '601' WHERE CIA_ID = 36;
update COMPANIAS set RAZON_SOCIAL = 'SEGUROS BBVA BANCOMER, S.A. DE C.V. GRUPO FINANCIERO BBVA BANCOMER', RFC = 'SBB961118TIA', LUGAR_EXPEDICION_CP = '11000', REGIMEN_FISCAL = '601' WHERE CIA_ID = 51;
update COMPANIAS set RAZON_SOCIAL = 'EL AGUILA COMPAÑIA DE SEGUROS, S.A. DE C.V.', RFC = 'ASE941124NN4', LUGAR_EXPEDICION_CP = '03200', REGIMEN_FISCAL = '601' WHERE CIA_ID = 53;
update COMPANIAS set RAZON_SOCIAL = 'A.N.A. COMPAÑIA DE SEGUROS, S.A. DE C.V.', RFC = 'ANA9509086E3', LUGAR_EXPEDICION_CP = '01050', REGIMEN_FISCAL = '601' WHERE CIA_ID = 61;
update COMPANIAS set RAZON_SOCIAL = 'PRIMERO SEGUROS, S.A. DE C.V.', RFC = 'PSE060223ITA', LUGAR_EXPEDICION_CP = '06600', REGIMEN_FISCAL = '601' WHERE CIA_ID = 70;