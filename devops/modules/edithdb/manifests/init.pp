class edithdb {
	
  database { 'edith': 
    ensure => 'present', 
    charset => 'utf8',
    provider => 'mysql', 
    require => Class['mysql::server'], 
  } 

  database { 'edith_test': 
    ensure => 'present', 
    charset => 'utf8',
    provider => 'mysql', 
    require => Class['mysql::server'], 
  } 

  database_user { 'edith@localhost': 
    ensure => 'present', 
    password_hash => mysql_password('edith'), 
    provider => 'mysql', 
    require => [Database['edith'], Database['edith_test']], 
  } 

  database_grant { 'edith@localhost/edith': 
    privileges => 'all', 
    provider => 'mysql', 
    require => Database_user['edith@localhost'], 
    notify => Exec['collations-edith'],
  }   

  database_grant { 'edith@localhost/edith_test': 
    privileges => 'all', 
    provider => 'mysql', 
    require => Database_user['edith@localhost'], 
    notify => Exec['collations-edith_test'],
  }   

  exec { "collations-edith":
    command => '/usr/bin/mysql edith -e "ALTER DATABASE edith COLLATE utf8_swedish_ci;"',
    logoutput => true,
    require => Database_grant['edith@localhost/edith'],
  }

  exec { "collations-edith_test":
    command => '/usr/bin/mysql edith_test -e "ALTER DATABASE edith_test COLLATE utf8_swedish_ci;"',
    logoutput => true,
    require => Database_grant['edith@localhost/edith_test'],
  }
}
