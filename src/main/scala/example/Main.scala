package example

import com.okta.authn.sdk.AuthenticationStateHandlerAdapter
import com.okta.authn.sdk.client.{ AuthenticationClient, AuthenticationClients }
import com.okta.authn.sdk.resource.{ AuthenticationRequest, AuthenticationResponse }
import com.okta.sdk.authc.credentials.TokenClientCredentials
import com.okta.sdk.client.{ Client, Clients }
import com.okta.sdk.resource.user._
import org.passay._

import java.util.UUID
import scala.sys.env

object Main extends App {
  val orgUrl = env("ORG_URL")
  val secret = env("CLIENT_SECRET")

  val oktaClient = Clients
    .builder()
    .setOrgUrl(orgUrl)
    .setClientCredentials(
      new TokenClientCredentials(secret)
    )
    .build()

  val oktaAuthClient = AuthenticationClients
    .builder()
    .setOrgUrl(orgUrl)
    .build()

  val id          = UUID.randomUUID().toString
  val userId      = s"$id@test.com"
  val oldPassword = generatePassword()
  val newPassword = generatePassword()

  val user = createUser(oktaClient)(userId, oldPassword)
  println(user)

  val passwordCredential =
    changePassword(oktaClient)(user, oldPassword, newPassword)
  println(passwordCredential)

  val authenticationResponse = authenticate(oktaAuthClient)(userId, newPassword)
  println(authenticationResponse)

  private def authenticate(authClient: AuthenticationClient)(
      userId: String,
      password: String
  ): AuthenticationResponse = {
    authClient.authenticate(
      authClient
        .instantiate(classOf[AuthenticationRequest])
        .setUsername(userId)
        .setPassword(password.toCharArray),
      new AuthenticationStateHandlerAdapter {
        override def handleUnknown(
            unknownResponse: AuthenticationResponse
        ): Unit = {
          println(unknownResponse)
        }
      }
    )
  }

  private def createUser(
      client: Client
  )(userId: String, password: String): User = {
    UserBuilder
      .instance()
      .setLogin(userId)
      .setFirstName(userId)
      .setLastName(userId)
      .setEmail(userId)
      .setPassword(password.toArray)
      .setActive(true)
      .buildAndCreate(client)
  }

  private def changePassword(client: Client)(
      user: User,
      oldPassword: String,
      newPassword: String
  ): UserCredentials = {
    user.changePassword(
      client
        .instantiate(classOf[ChangePasswordRequest])
        .setOldPassword(
          client
            .instantiate(classOf[PasswordCredential])
            .setValue(oldPassword.toArray)
        )
        .setNewPassword(
          client
            .instantiate(classOf[PasswordCredential])
            .setValue(newPassword.toArray)
        )
    )
  }

  private def generatePassword(): String = {
    val generator = new PasswordGenerator()

    val lowerCaseChars = EnglishCharacterData.LowerCase
    val lowerCaseRule  = new CharacterRule(lowerCaseChars)
    lowerCaseRule.setNumberOfCharacters(2)

    val upperCaseChars = EnglishCharacterData.UpperCase
    val upperCaseRule  = new CharacterRule(upperCaseChars)
    upperCaseRule.setNumberOfCharacters(2)

    val digitChars = EnglishCharacterData.Digit
    val digitRule  = new CharacterRule(digitChars)
    digitRule.setNumberOfCharacters(2)

    val specialChars = new CharacterData() {
      def getErrorCode: String = "001"

      def getCharacters = "!@#$%^&*()_+"
    }
    val splCharRule = new CharacterRule(specialChars)
    splCharRule.setNumberOfCharacters(2)

    generator.generatePassword(
      10,
      splCharRule,
      lowerCaseRule,
      upperCaseRule,
      digitRule
    )
  }

}
