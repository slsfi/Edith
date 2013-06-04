exec {'apt-update':
  command => '/usr/bin/apt-get update',
}

Exec['apt-update'] -> Package <| |>

node default {

  include apt
  include stdlib
  include java
  include git
  include mysql
  include maven

  include edithdb

  class { 'mysql::server':
    config_hash => {
      'root_password' => 'wert',
    },
  }
}