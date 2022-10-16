package common.jdbc;

public class test_jdbc
{
	// Идентификаторы параметров подключения 
	private  final  int  HOST      = 0;   //  хост  (компьютер)
	private  final  int  SCHEMA    = 1;   //  схема (база данных)
	private  final  int  LOGIN     = 2;   //  логин подключения
	private  final  int  PWD       = 3;   //  пароль подключения
	private  final  int  DBMS      = 4;   //  СУБД
	private  final  int  ACTIVE    = 5;   //  флаг активности
	private  String[][]  params    = 
      {{"localhost"            , ""  , "login", "password", "MySQL"   , "true"},  // mysql 
       {"localhost\\SQLEXPRESS", ""  , "login", "password", "MS SQL"  , "true"},  // mssql
       {"127.0.0.1"            , ""  , "admin", "admin", "Postgres", "true"},  // postgres
       {"localhost"            , "XE", "login", "password", "Oracle"  , "true"},  // oracle
       {""                     , "db", ""     , ""        , "Derby"   , "true"}}; // derby	  
	private  final  int [] ports      = {3306, 1433, 5432, 1521, 0};              // порты СУБД
	private  final  int  idx_mssql    = 1;
	private  final  int  idx_postgres = 2;
	private  final  int  idx_oracle   = 3;
	private  final  int  idx_derby    = 4;
	
	private  boolean schema_created = false;
	private  boolean table_created  = false;

	private  final  String SCHEMA_NAME = "MEDIA"                  ;

	private  final  String OBJ_SCHEMA  = "Schema 'MEDIA'"         ;
	private  final  String OBJ_TABLE   = "Table 'USERS'"          ;

	private  final  String OBJ_CREATED = "\t%s created";
	private  final  String OBJ_DROPED  = "\t%s deleted" ;
	private  final  String OBJ_NOT_CR  = "\t%s not created" ;
	private  final  String OBJ_NOT_DEL = "\t%s not created" ;

	private  final  String SUCCESS     = "%s connection : success";
	private  final  String FAILED      = "%s connection : failed" ;
	
	private  final  String TABLE_DROP   = "DROP TABLE %sUSERS"      ;
	private  final  String TABLE_CREATE = "CREATE TABLE %sUSERS( "  +
                                  "id    INT PRIMARY KEY, "          +
                                  "name  VARCHAR(128) NOT NULL, "    +
                                  "data  TIMESTAMP )" ;
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * Поцедура подключения к серверу БД
	 * @param dao модуль доступа
	 * @param idx идентификатор сервера СУБД
	 */
	private void createConnecion (dao_base dao, final int idx)
	{
		// Формирование строки подключения
		dao.setURL(params[idx][HOST], params[idx][SCHEMA], ports[idx]);
		// Подключение к серверу
		dao.Connect(params[idx][LOGIN], params[idx][PWD]);
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * Процедура создания объектов СУБД
	 * @param dao модуль доступа к СУБД
	 * @param idx идентификатор СУБД
	 */
	private void createObjects(dao_base dao, final int idx)
	{
		// Создание схемы
		if (idx != idx_oracle) {
			// Создание схемы
			schema_created = dao.createSchema(SCHEMA_NAME);
			if (schema_created) {
				 // Schema created
				System.out.println (String.format(OBJ_CREATED, OBJ_SCHEMA));
			} else
				 // Schema not created
				System.err.println (String.format(OBJ_NOT_CR, OBJ_SCHEMA));
			// Переподключение к вновь созданной схеме
			if ((idx != idx_derby) && (idx != idx_postgres)) {
				dao.Disconnect(dao.getConnection());
				params[idx][SCHEMA] = SCHEMA_NAME;
				createConnecion (dao, idx);
			}
		}
		// Создание таблицы
		if (schema_created || (idx == idx_oracle)) {
			if (((idx == idx_derby) || (idx == idx_postgres)))
				table_created = dao.execSQL(String.format(TABLE_CREATE, "\"" + SCHEMA_NAME + "\"."));
			else
				table_created = dao.execSQL(String.format(TABLE_CREATE, ""));
			if (table_created)
				 // Table created
				System.out.println (String.format(OBJ_CREATED, OBJ_TABLE));
			else
				 // Table not created
				System.err.println (String.format(OBJ_NOT_CR, OBJ_TABLE));
		}
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * Процедура удаления объектов СУБД
	 * @param dao модуль доступа к СУБД
	 * @param idx идентификатор СУБД
	 */
	private void dropObjects(dao_base dao, final int idx)
	{
		// Удаление таблицы
		if (table_created) {
			if ((idx != idx_derby) && (idx != idx_postgres)) {
				if (dao.execSQL(String.format(TABLE_DROP, "")))
					// Table deleted
					System.out.println (String.format(OBJ_DROPED, OBJ_TABLE));
				else
					// Table not deleted
					System.err.println (String.format(OBJ_NOT_DEL, OBJ_TABLE));
			} else {
				if (dao.execSQL(String.format(TABLE_DROP, "\"" + SCHEMA_NAME + "\".")))
					// Table deleted
					System.out.println (String.format(OBJ_DROPED, OBJ_TABLE));
				else
					// Table not deleted
					System.err.println (String.format(OBJ_NOT_DEL, OBJ_TABLE));
			}
		}
		// Отключение от вновь созданной схемы
		if (idx == idx_mssql) {
			// Отключение от схемы для удаления
			dao.Disconnect(dao.getConnection());
			// Очищаем наименование схемы
			params[idx][SCHEMA] = "";
			// Подключение к СУБД
			createConnecion (dao, idx);
		}

		// Удаление схемы
		if (idx != idx_oracle) {
			if (dao.dropSchema(SCHEMA_NAME)) {
				// Schema deleted
				System.out.println (String.format(OBJ_DROPED, OBJ_SCHEMA));
			} else
				// Schema not deleted
				System.err.println (String.format(OBJ_NOT_DEL, OBJ_SCHEMA));
		}
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public test_jdbc()
	{
		// Определение "модуля доступа к СУБД"
		dao_base dao = null;
		// Цикл перебора параметров подключения к СУБД
		i = 2;
			// Проверка флага активности СУБД
			if (params[i][ACTIVE].equalsIgnoreCase(String.valueOf(true))) {
				// Создание "модуля доступа" к СУБД 
				switch (i) {
					case 0 : dao = new dao_mysql   (); break;
					case 1 : dao = new dao_mssql   (); break;
					case 2 : dao = new dao_postgres(); break;
					case 3 : dao = new dao_oracle  (); break;
					case 4 : dao = new dao_derby   (); break;
				}
			}
			// Проверка создания dm
			if (dao != null) {
				// Подключение к серверу БД
				createConnecion (dao, i);
				// Вывод в консоль информации о подключении
				if (dao.getConnection() != null) {
					System.out.println (String.format(SUCCESS, params[i][DBMS]));
					// Создание схемы и таблицы
					createObjects(dao, i);
					// Удаление таблицы и схемы
					dropObjects(dao, i);
					// Отключение от сервера БД
					dao.Disconnect(dao.getConnection());
				} else
					System.err.println (String.format(FAILED, params[i][DBMS]));
			}
			dao = null;
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static void main(String[] args)
	{
		new test_jdbc();
		System.exit(0);
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
