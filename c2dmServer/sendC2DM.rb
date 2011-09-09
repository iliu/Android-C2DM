#!/usr/bin/env ruby

require 'rubygems'
require 'c2dm'

settings = YAML::parse( File.open( "config.yml" ) ).transform
regId = settings['regId']
email = settings['email']
password = settings['password']

if ARGV.empty? 
  puts 'Usage: ./sendC2DM "message to be sent"'
  exit
end

puts 'Sending C2DM Packet...'
c2dm = C2DM.new(email, password, settings['app_name'])
notification = { :registration_id => regId, :data => { :message => ARGV[0] }, :collapse_key => "biteme" }
c2dm.send_notification(notification)




