//config.devServer.historyApiFallback = true;

//config.devServer.host = '0.0.0.0';//your ip address
//    config.devServer.port = 8080;
//    config.devServer.disableHostCheck= true;

module.exports = {
   // devServer.historyApiFallback = true;

/*    devServer: {
      //  contentBase: 'app/ui/www',
        //  devtool: 'eval',
        // hot: true,
        //  inline: true,
        //  port: 3000,
        //  outputPath: buildPath,
        historyApiFallback: true,
    },*/

    //...
    optimization: {
        splitChunks: {
            chunks: 'all',
            name: true,
            minSize: 10,
            minRemainingSize: 0,
            maxSize: 500,
            minChunks: 1,
            maxAsyncRequests: 30,
            maxInitialRequests: 30,
            automaticNameDelimiter: '~',
            enforceSizeThreshold: 50000,
            cacheGroups: {
                defaultVendors: {
                    test: /[\\/]node_modules[\\/]/,
                    priority: -10,
                    reuseExistingChunk: true
                },
                default: {
                    minChunks: 2,
                    priority: -20,
                    reuseExistingChunk: true
                }
            }
        }
    }
};