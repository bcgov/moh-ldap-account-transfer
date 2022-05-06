import { Selector } from 'testcafe'

class HomePage {
  constructor() {
    this.homeLink = Selector('#home-link')
    this.errorHelp = Selector('#error-help > p')
    this.heading = Selector('h1')
    this.errorPanel = Selector('div').withAttribute('class', 'error-message')
    this.errorText = Selector('div').withAttribute('class', 'error-text')
    this.usernameInput = Selector('#username')
    this.passwordInput = Selector('#password')
    this.submitButton = Selector('button[type="submit"]')
  }
}

export default new HomePage()
