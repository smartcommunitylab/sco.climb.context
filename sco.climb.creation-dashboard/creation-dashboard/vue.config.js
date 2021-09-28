module.exports = {
  transpileDependencies: [
    'vuetify'
  ],

  pluginOptions: {
    i18n: {
      locale: 'it',
      fallbackLocale: 'it',
      localeDir: 'locales',
      enableInSFC: true
    }
  },
  devServer: {
    host: 'localhost',
  }
}
