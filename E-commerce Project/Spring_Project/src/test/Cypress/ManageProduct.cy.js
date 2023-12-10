describe('Manage Product', () => {
  it('passes', () => {
    cy.visit('http://localhost:8080/');
      cy.get('.btn-secondary').click();
      cy.get('#username').type('admin');
      cy.get('#password').type('aaa');
      cy.get('.btn').click();
      cy.get(':nth-child(2) > .card > .card-body > .card-link').click()
      cy.get('.btn-primary').click()

      const currentTime = new Date().toLocaleTimeString();
      const newText =  'Test Product' + currentTime;

      cy.on('window:alert', (alertMessage) => {

        expect(alertMessage).to.contain('Product Added Successfully'); 
      });

      cy.get(':nth-child(1) > :nth-child(2) > .form-control').type(newText)
      cy.get(':nth-child(3) > .form-control').select('Test Category')
      cy.get(':nth-child(1) > :nth-child(4) > .form-control').type('1000')
      cy.get(':nth-child(5) > .form-control').type('500')
      cy.get(':nth-child(6) > .form-control').type('100')
      cy.get(':nth-child(2) > :nth-child(2) > .form-control').type('Testing By QCFORCEx')
      cy.get(':nth-child(2) > :nth-child(4) > .form-control').type('https://m.media-amazon.com/images/M/MV5BMThlOWE3MWEtZjM4Ny00M2FiLTkyMmYtZGY3ZTcyMzM5YmNlXkEyXkFqcGdeQWpnYW1i._V1_.jpg')

      cy.get('.btn').click()
  })
})