package com.hao.base.export.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.expression.spel.InternalParseException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.util.Assert;
/**
 * 
 * @author zhaozhihao
 * @createTime 2017年7月24日 下午3:14:07	
 * @version 1.0
 */
public class Tokenizer {
	// if this is changed, it must remain sorted
		private static final String[] ALTERNATIVE_OPERATOR_NAMES = { "DIV", "EQ", "GE", "GT",
			"LE", "LT", "MOD", "NE", "NOT" };

		private static final byte FLAGS[] = new byte[256];

		private static final byte IS_DIGIT = 0x01;

		private static final byte IS_HEXDIGIT = 0x02;

		private static final byte IS_ALPHA = 0x04;

		static {
			for (int ch = '0'; ch <= '9'; ch++) {
				FLAGS[ch] |= IS_DIGIT | IS_HEXDIGIT;
			}
			for (int ch = 'A'; ch <= 'F'; ch++) {
				FLAGS[ch] |= IS_HEXDIGIT;
			}
			for (int ch = 'a'; ch <= 'f'; ch++) {
				FLAGS[ch] |= IS_HEXDIGIT;
			}
			for (int ch = 'A'; ch <= 'Z'; ch++) {
				FLAGS[ch] |= IS_ALPHA;
			}
			for (int ch = 'a'; ch <= 'z'; ch++) {
				FLAGS[ch] |= IS_ALPHA;
			}
		}


		String expressionString;

		char[] toProcess;

		int pos;

		int max;

		List<Token> tokens = new ArrayList<Token>();


		public Tokenizer(String inputData) {
			this.expressionString = inputData;
			this.toProcess = (inputData + "\0").toCharArray();
			this.max = this.toProcess.length;
			this.pos = 0;
			process();
		}


		public void process() {
			while (this.pos < this.max) {
				char ch = this.toProcess[this.pos];
				if (isAlphabetic(ch)) {
					lexIdentifier();
				}
				else {
					switch (ch) {
						case '+':
							if (isTwoCharToken(TokenKind.INC)) {
								pushPairToken(TokenKind.INC);
							}
							else {
								pushCharToken(TokenKind.PLUS);
							}
							break;
						case '_': // the other way to start an identifier
							lexIdentifier();
							break;
						case '-':
							if (isTwoCharToken(TokenKind.DEC)) {
								pushPairToken(TokenKind.DEC);
							}
							else {
								pushCharToken(TokenKind.MINUS);
							}
							break;
						case ':':
							pushCharToken(TokenKind.COLON);
							break;
						case '.':
							pushCharToken(TokenKind.DOT);
							break;
						case ',':
							pushCharToken(TokenKind.COMMA);
							break;
						case '*':
							pushCharToken(TokenKind.STAR);
							break;
						case '/':
							pushCharToken(TokenKind.DIV);
							break;
						case '%':
							pushCharToken(TokenKind.MOD);
							break;
						case '(':
							pushCharToken(TokenKind.LPAREN);
							break;
						case ')':
							pushCharToken(TokenKind.RPAREN);
							break;
						case '[':
							pushCharToken(TokenKind.LSQUARE);
							break;
						case '#':
							pushCharToken(TokenKind.HASH);
							break;
						case ']':
							pushCharToken(TokenKind.RSQUARE);
							break;
						case '{':
							pushCharToken(TokenKind.LCURLY);
							break;
						case '}':
							pushCharToken(TokenKind.RCURLY);
							break;
						case '@':
							pushCharToken(TokenKind.BEAN_REF);
							break;
						case '^':
							if (isTwoCharToken(TokenKind.SELECT_FIRST)) {
								pushPairToken(TokenKind.SELECT_FIRST);
							}
							else {
								pushCharToken(TokenKind.POWER);
							}
							break;
						case '!':
							if (isTwoCharToken(TokenKind.NE)) {
								pushPairToken(TokenKind.NE);
							}
							else if (isTwoCharToken(TokenKind.PROJECT)) {
								pushPairToken(TokenKind.PROJECT);
							}
							else {
								pushCharToken(TokenKind.NOT);
							}
							break;
						case '=':
							if (isTwoCharToken(TokenKind.EQ)) {
								pushPairToken(TokenKind.EQ);
							}
							else {
								pushCharToken(TokenKind.ASSIGN);
							}
							break;
						case '&':
							if (!isTwoCharToken(TokenKind.SYMBOLIC_AND)) {
								throw new InternalParseException(new SpelParseException(
										this.expressionString, this.pos, SpelMessage.MISSING_CHARACTER,
										"&"));
							}
							pushPairToken(TokenKind.SYMBOLIC_AND);
							break;
						case '|':
							if (!isTwoCharToken(TokenKind.SYMBOLIC_OR)) {
								throw new InternalParseException(new SpelParseException(
										this.expressionString, this.pos, SpelMessage.MISSING_CHARACTER,
										"|"));
							}
							pushPairToken(TokenKind.SYMBOLIC_OR);
							break;
						case '?':
							if (isTwoCharToken(TokenKind.SELECT)) {
								pushPairToken(TokenKind.SELECT);
							}
							else if (isTwoCharToken(TokenKind.ELVIS)) {
								pushPairToken(TokenKind.ELVIS);
							}
							else if (isTwoCharToken(TokenKind.SAFE_NAVI)) {
								pushPairToken(TokenKind.SAFE_NAVI);
							}
							else {
								pushCharToken(TokenKind.QMARK);
							}
							break;
						case '$':
							if (isTwoCharToken(TokenKind.SELECT_LAST)) {
								pushPairToken(TokenKind.SELECT_LAST);
							}
							else {
								lexIdentifier();
							}
							break;
						case '>':
							if (isTwoCharToken(TokenKind.GE)) {
								pushPairToken(TokenKind.GE);
							}
							else {
								pushCharToken(TokenKind.GT);
							}
							break;
						case '<':
							if (isTwoCharToken(TokenKind.LE)) {
								pushPairToken(TokenKind.LE);
							}
							else {
								pushCharToken(TokenKind.LT);
							}
							break;
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							lexNumericLiteral(ch == '0');
							break;
						case ' ':
						case '\t':
						case '\r':
						case '\n':
							// drift over white space
							this.pos++;
							break;
						case '\'':
							lexQuotedStringLiteral();
							break;
						case '"':
							lexDoubleQuotedStringLiteral();
							break;
						case 0:
							// hit sentinel at end of value
							this.pos++; // will take us to the end
							break;
						case '\\':
							throw new InternalParseException(
									new SpelParseException(this.expressionString, this.pos, SpelMessage.UNEXPECTED_ESCAPE_CHAR));
						default:
							throw new IllegalStateException("Cannot handle (" + Integer.valueOf(ch) + ") '" + ch + "'");
					}
				}
			}
		}

		public List<Token> getTokens() {
			return this.tokens;
		}

		// STRING_LITERAL: '\''! (APOS|~'\'')* '\''!;
		public void lexQuotedStringLiteral() {
			int start = this.pos;
			boolean terminated = false;
			while (!terminated) {
				this.pos++;
				char ch = this.toProcess[this.pos];
				if (ch == '\'') {
					// may not be the end if the char after is also a '
					if (this.toProcess[this.pos + 1] == '\'') {
						this.pos++; // skip over that too, and continue
					}
					else {
						terminated = true;
					}
				}
				if (ch == 0) {
					throw new InternalParseException(new SpelParseException(this.expressionString, start,
							SpelMessage.NON_TERMINATING_QUOTED_STRING));
				}
			}
			this.pos++;
			this.tokens.add(new Token(TokenKind.LITERAL_STRING, subarray(start, this.pos), start, this.pos));
		}

		// DQ_STRING_LITERAL: '"'! (~'"')* '"'!;
		public void lexDoubleQuotedStringLiteral() {
			int start = this.pos;
			boolean terminated = false;
			while (!terminated) {
				this.pos++;
				char ch = this.toProcess[this.pos];
				if (ch == '"') {
					// may not be the end if the char after is also a "
					if (this.toProcess[this.pos + 1] == '"') {
						this.pos++; // skip over that too, and continue
					}
					else {
						terminated = true;
					}
				}
				if (ch == 0) {
					throw new InternalParseException(new SpelParseException(this.expressionString,
							start, SpelMessage.NON_TERMINATING_DOUBLE_QUOTED_STRING));
				}
			}
			this.pos++;
			this.tokens.add(new Token(TokenKind.LITERAL_STRING, subarray(start, this.pos), start, this.pos));
		}

		// REAL_LITERAL :
		// ('.' (DECIMAL_DIGIT)+ (EXPONENT_PART)? (REAL_TYPE_SUFFIX)?) |
		// ((DECIMAL_DIGIT)+ '.' (DECIMAL_DIGIT)+ (EXPONENT_PART)? (REAL_TYPE_SUFFIX)?) |
		// ((DECIMAL_DIGIT)+ (EXPONENT_PART) (REAL_TYPE_SUFFIX)?) |
		// ((DECIMAL_DIGIT)+ (REAL_TYPE_SUFFIX));
		// fragment INTEGER_TYPE_SUFFIX : ( 'L' | 'l' );
		// fragment HEX_DIGIT :
		// '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'|'A'|'B'|'C'|'D'|'E'|'F'|'a'|'b'|'c'|'d'|'e'|'f';
		//
		// fragment EXPONENT_PART : 'e' (SIGN)* (DECIMAL_DIGIT)+ | 'E' (SIGN)*
		// (DECIMAL_DIGIT)+ ;
		// fragment SIGN : '+' | '-' ;
		// fragment REAL_TYPE_SUFFIX : 'F' | 'f' | 'D' | 'd';
		// INTEGER_LITERAL
		// : (DECIMAL_DIGIT)+ (INTEGER_TYPE_SUFFIX)?;

		public void lexNumericLiteral(boolean firstCharIsZero) {
			boolean isReal = false;
			int start = this.pos;
			char ch = this.toProcess[this.pos + 1];
			boolean isHex = ch == 'x' || ch == 'X';

			// deal with hexadecimal
			if (firstCharIsZero && isHex) {
				this.pos = this.pos + 1;
				do {
					this.pos++;
				}
				while (isHexadecimalDigit(this.toProcess[this.pos]));
				if (isChar('L', 'l')) {
					pushHexIntToken(subarray(start + 2, this.pos), true, start, this.pos);
					this.pos++;
				}
				else {
					pushHexIntToken(subarray(start + 2, this.pos), false, start, this.pos);
				}
				return;
			}

			// real numbers must have leading digits

			// Consume first part of number
			do {
				this.pos++;
			}
			while (isDigit(this.toProcess[this.pos]));

			// a '.' indicates this number is a real
			ch = this.toProcess[this.pos];
			if (ch == '.') {
				isReal = true;
				int dotpos = this.pos;
				// carry on consuming digits
				do {
					this.pos++;
				}
				while (isDigit(this.toProcess[this.pos]));
				if (this.pos == dotpos + 1) {
					// the number is something like '3.'. It is really an int but may be
					// part of something like '3.toString()'. In this case process it as
					// an int and leave the dot as a separate token.
					this.pos = dotpos;
					pushIntToken(subarray(start, this.pos), false, start, this.pos);
					return;
				}
			}

			int endOfNumber = this.pos;

			// Now there may or may not be an exponent

			// is it a long ?
			if (isChar('L', 'l')) {
				if (isReal) { // 3.4L - not allowed
					throw new InternalParseException(new SpelParseException(this.expressionString,
							start, SpelMessage.REAL_CANNOT_BE_LONG));
				}
				pushIntToken(subarray(start, endOfNumber), true, start, endOfNumber);
				this.pos++;
			}
			else if (isExponentChar(this.toProcess[this.pos])) {
				isReal = true; // if it wasn't before, it is now
				this.pos++;
				char possibleSign = this.toProcess[this.pos];
				if (isSign(possibleSign)) {
					this.pos++;
				}

				// exponent digits
				do {
					this.pos++;
				}
				while (isDigit(this.toProcess[this.pos]));
				boolean isFloat = false;
				if (isFloatSuffix(this.toProcess[this.pos])) {
					isFloat = true;
					endOfNumber = ++this.pos;
				}
				else if (isDoubleSuffix(this.toProcess[this.pos])) {
					endOfNumber = ++this.pos;
				}
				pushRealToken(subarray(start, this.pos), isFloat, start, this.pos);
			}
			else {
				ch = this.toProcess[this.pos];
				boolean isFloat = false;
				if (isFloatSuffix(ch)) {
					isReal = true;
					isFloat = true;
					endOfNumber = ++this.pos;
				}
				else if (isDoubleSuffix(ch)) {
					isReal = true;
					endOfNumber = ++this.pos;
				}
				if (isReal) {
					pushRealToken(subarray(start, endOfNumber), isFloat, start, endOfNumber);
				}
				else {
					pushIntToken(subarray(start, endOfNumber), false, start, endOfNumber);
				}
			}
		}

		public void lexIdentifier() {
			int start = this.pos;
			do {
				this.pos++;
			}
			while (isIdentifier(this.toProcess[this.pos]));
			char[] subarray = subarray(start, this.pos);

			// Check if this is the alternative (textual) representation of an operator (see
			// alternativeOperatorNames)
			if ((this.pos - start) == 2 || (this.pos - start) == 3) {
				String asString = new String(subarray).toUpperCase();
				int idx = Arrays.binarySearch(ALTERNATIVE_OPERATOR_NAMES, asString);
				if (idx >= 0) {
					pushOneCharOrTwoCharToken(TokenKind.valueOf(asString), start, subarray);
					return;
				}
			}
			this.tokens.add(new Token(TokenKind.IDENTIFIER, subarray, start, this.pos));
		}

		public void pushIntToken(char[] data, boolean isLong, int start, int end) {
			if (isLong) {
				this.tokens.add(new Token(TokenKind.LITERAL_LONG, data, start, end));
			}
			else {
				this.tokens.add(new Token(TokenKind.LITERAL_INT, data, start, end));
			}
		}

		public void pushHexIntToken(char[] data, boolean isLong, int start, int end) {
			if (data.length == 0) {
				if (isLong) {
					throw new InternalParseException(new SpelParseException(this.expressionString,
							start, SpelMessage.NOT_A_LONG, this.expressionString.substring(start,
									end + 1)));
				}
				else {
					throw new InternalParseException(new SpelParseException(this.expressionString,
							start, SpelMessage.NOT_AN_INTEGER, this.expressionString.substring(
									start, end)));
				}
			}
			if (isLong) {
				this.tokens.add(new Token(TokenKind.LITERAL_HEXLONG, data, start, end));
			}
			else {
				this.tokens.add(new Token(TokenKind.LITERAL_HEXINT, data, start, end));
			}
		}

		public void pushRealToken(char[] data, boolean isFloat, int start, int end) {
			if (isFloat) {
				this.tokens.add(new Token(TokenKind.LITERAL_REAL_FLOAT, data, start, end));
			}
			else {
				this.tokens.add(new Token(TokenKind.LITERAL_REAL, data, start, end));
			}
		}

		public char[] subarray(int start, int end) {
			char[] result = new char[end - start];
			System.arraycopy(this.toProcess, start, result, 0, end - start);
			return result;
		}

		/**
		 * Check if this might be a two character token.
		 */
		public boolean isTwoCharToken(TokenKind kind) {
			Assert.isTrue(kind.tokenChars.length == 2);
			Assert.isTrue(this.toProcess[this.pos] == kind.tokenChars[0]);
			return this.toProcess[this.pos + 1] == kind.tokenChars[1];
		}

		/**
		 * Push a token of just one character in length.
		 */
		public void pushCharToken(TokenKind kind) {
			this.tokens.add(new Token(kind, this.pos, this.pos + 1));
			this.pos++;
		}

		/**
		 * Push a token of two characters in length.
		 */
		public void pushPairToken(TokenKind kind) {
			this.tokens.add(new Token(kind, this.pos, this.pos + 2));
			this.pos += 2;
		}

		public void pushOneCharOrTwoCharToken(TokenKind kind, int pos, char[] data) {
			this.tokens.add(new Token(kind, data, pos, pos + kind.getLength()));
		}

		// ID: ('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'$'|'0'..'9'|DOT_ESCAPED)*;
		public boolean isIdentifier(char ch) {
			return isAlphabetic(ch) || isDigit(ch) || ch == '_' || ch == '$';
		}

		public boolean isChar(char a, char b) {
			char ch = this.toProcess[this.pos];
			return ch == a || ch == b;
		}

		public boolean isExponentChar(char ch) {
			return ch == 'e' || ch == 'E';
		}

		public boolean isFloatSuffix(char ch) {
			return ch == 'f' || ch == 'F';
		}

		public boolean isDoubleSuffix(char ch) {
			return ch == 'd' || ch == 'D';
		}

		public boolean isSign(char ch) {
			return ch == '+' || ch == '-';
		}

		public boolean isDigit(char ch) {
			if (ch > 255) {
				return false;
			}
			return (FLAGS[ch] & IS_DIGIT) != 0;
		}

		public boolean isAlphabetic(char ch) {
			if (ch > 255) {
				return false;
			}
			return (FLAGS[ch] & IS_ALPHA) != 0;
		}

		public boolean isHexadecimalDigit(char ch) {
			if (ch > 255) {
				return false;
			}
			return (FLAGS[ch] & IS_HEXDIGIT) != 0;
		}

}
