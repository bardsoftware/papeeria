import async from "async";
import escape from "escape-html";
import fs from "fs";
import path from "path";
import latex from "latex";
import sanitize from "sanitize-filename";
import sprite from "css-sprite";
import {convert} from "imagemagick";

const dataFiles = path.join(__dirname, "data");
const distFiles = path.join(__dirname, "dist");
const spriteAssets = path.join(distFiles, "assets");
const symbols = require(path.join(dataFiles, "symbols.json"));
const spriteConfig = {
  src: [path.join(spriteAssets, "*.png")],
  out: path.join(distFiles, "img"),
  name: "sprites",
  style: path.join(distFiles, "_sprites.less"),
  cssPath: "./img",
  processor: "less",
  prefix: "symbol",
  template: path.join(dataFiles, "less.mustache")
};

// combine all symbols in one array
let allSymbols = Object.keys(symbols).reduce((prev, curr) => {
  return prev.concat(symbols[curr])
}, []);

spriteFromListOfSymbols(allSymbols);
// XXX: uncomment to print html env
// htmlFromListOfSymbols(symbols);

function spriteFromListOfSymbols(listOfSymbols) {
  async.each(listOfSymbols, (symbol, callback) => {
    let pdfFileName = `${convertSymbolName(symbol)}.pdf`;
    let pdfFilePath = path.join(spriteAssets, pdfFileName);
    if (fs.existsSync(pdfFilePath)) {
      // I don't think there may be more than two equal symbol names (e.g. \gamma, \Gamma)
      pdfFilePath = path.join(spriteAssets, `_${pdfFileName}`);
    }
    let pdfFileWriteStream = fs.createWriteStream(pdfFilePath);

    latex(getTexEnv(symbol), { format: "pdf" }).pipe(pdfFileWriteStream);
    pdfFileWriteStream.on("finish", () => convertPdfToPng(pdfFilePath, callback));
  }, (err) => {
    if (err) {
      throw err;
    }
    createSprite();
  });
}

function htmlFromListOfSymbols(symbols) {
  Object.keys(symbols).forEach((key) => {
    console.log(`\n${key}\n`);
    console.log(getHtmlEnv(symbols[key]));
  });
}

function convertSymbolName(symbol) {
  let s = symbol
            .replace(/[\+]+/g, "plus")
            .replace(/[\-]+/g, "minus")
            .replace(/[\=]/g, "equals")
            .replace(/[\<]/g, "lt")
            .replace(/[\>]/g, "gt")
            .replace(/[\|]/g, "verticalLine")
            .replace(/[\.]/g, "dot")
            .replace(/[\,]/g, "comma")
            .replace(/[\;]/g, "semicolon")
            .replace(/[\(]/g, "lparen")
            .replace(/[\)]/g, "rparen")
            .replace(/[\[]/g, "lbracket")
            .replace(/[\]]/g, "rbracket")
            .replace(/\{.*\}/g, "")
            .replace(/[\{]/g, "lcurlybracket")
            .replace(/[\}]/g, "rcurlybracket")
            .replace(/[\/]/g, "trueslash");
  return sanitize(s);
}

function createSprite() {
  sprite.create(spriteConfig, () => {
    fs.readdir(spriteAssets, (err, files) => {
      files.forEach((fileName) => {
        fs.unlink(path.join(spriteAssets, fileName));
      });
    });
  });
}

function convertPdfToPng(pdfFilePath, callback) {
  convert(pdfToPngWithTranparency(pdfFilePath), (err, stdout) => {
    if (err) {
      throw err;
    }
    callback();
  });
}

function pdfToPngWithTranparency(pathToPdf) {
  let pathToPng = `${pathToPdf.slice(0, -4)}.png`;
  return [pathToPdf, "-transparent", "#ffffff", "-trim", pathToPng];
}

function getTexEnv(symbol) {
  return [
    "\\documentclass[varwidth,convert]{standalone}",
    "\\usepackage{amsmath,amsthm,amssymb,amsfonts}",
    "\\begin{document}",
    "\\noindent",
    "\\\\",
    `\\Huge\$${symbol}\$`,
    "\\\\",
    "\\end{document}"
  ];
}

function getHtmlEnv(symbols) {
  let s = `<div class="character-symbols">\n`;
  symbols.forEach((symbol) => {
    let escapedSymbol = escape(symbol).replace(/\\u/, "&#92;u")
    s += `<div class="symbol"><i class="s-${convertSymbolName(symbol)}"></i><code>${escapedSymbol}</code></div>\n`
  });
  return s + "</div>";
}

export default () => {};
