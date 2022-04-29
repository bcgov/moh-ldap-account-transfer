import { SITE_UNDER_TEST } from '../configuration'
import AlertPage from '../pages/AlertPage.js'
import HomePage from '../pages/HomePage.js'
import { regularAccUser } from '../roles/roles'

const ERROR_MESSAGE = 'Please correct errors before submitting'
const USERNAME_REQUIRED = 'Username is required'
const PASSWORD_REQUIRED = 'Password is required'
const INVALID_USER_PASS = 'Invalid Username or Password'

fixture(`Home Page`).disablePageCaching`Test Home Page`
  .beforeEach(async (t) => {
    await t.useRole(regularAccUser)
  })
  .page(SITE_UNDER_TEST)

test('Check Home tab is clickable ', async (t) => {
  await t.click(HomePage.homeLink)
})

test('Check heading exists on Home page! ', async (t) => {
  await t.click(HomePage.homeLink).expect(HomePage.heading.innerText).eql('Welcome to the MSP Direct Account Transfer')
})

test('Check required fields validation', async (t) => {
  await t.click(HomePage.submitButton).expect(HomePage.errorText.nth(0).textContent).eql(USERNAME_REQUIRED).expect(HomePage.errorText.nth(1).textContent).eql(PASSWORD_REQUIRED)
})

test('Check server validation', async (t) => {
  await t.typeText(HomePage.usernameInput, 'abc').typeText(HomePage.passwordInput, '123').click(HomePage.submitButton).expect(AlertPage.alertBannerText.textContent).eql(INVALID_USER_PASS).expect(HomePage.errorHelp.textContent).contains('There was an error with your transfer request.')
})
