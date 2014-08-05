'use strict';

/* https://github.com/angular/protractor/blob/master/docs/getting-started.md */

describe('my app', function() {

  browser.get('index.html');

  it('should automatically redirect to /telemetry when location hash/fragment is empty', function() {
    expect(browser.getLocationAbsUrl()).toMatch("/telemetry");
  });


  describe('telemetry', function() {

    beforeEach(function() {
      browser.get('index.html#/telemetry');
    });


    it('should render telemetry when user navigates to /telemetry', function() {
      expect(element.all(by.css('[ng-view] p')).first().getText()).
        toMatch(/Altitude/);
    });

  });
});
