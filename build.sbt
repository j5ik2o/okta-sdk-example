name := "okta-sdk-examples"
version := "0.1"
scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "com.okta.sdk"       % "okta-sdk-api"        % "4.0.0",
  "com.okta.sdk"       % "okta-sdk-impl"       % "4.0.0",
  "com.okta.authn.sdk" % "okta-authn-sdk-api"  % "2.0.2",
  "com.okta.authn.sdk" % "okta-authn-sdk-impl" % "2.0.2",
  "com.okta.sdk"       % "okta-sdk-okhttp"     % "4.0.0",
  "org.passay"         % "passay"              % "1.6.0"
)

addCommandAlias("fmt", ";scalafmtAll;scalafmtSbt")
