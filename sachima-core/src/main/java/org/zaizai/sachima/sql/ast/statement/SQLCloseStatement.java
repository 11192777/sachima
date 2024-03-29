/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaizai.sachima.sql.ast.statement;

import org.zaizai.sachima.sql.ast.SQLName;
import org.zaizai.sachima.sql.ast.SQLObject;
import org.zaizai.sachima.sql.ast.SQLStatementImpl;
import org.zaizai.sachima.sql.ast.expr.SQLIdentifierExpr;
import org.zaizai.sachima.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

/**
 * 
 * MySql cursor close statement
 * @author zz [455910092@qq.com]
 */
public class SQLCloseStatement extends SQLStatementImpl{
	
	//cursor name
	private SQLName cursorName;
	
	public SQLName getCursorName() {
		return cursorName;
	}

	public void setCursorName(String cursorName) {
		setCursorName(new SQLIdentifierExpr(cursorName));
	}
	
	public void setCursorName(SQLName cursorName) {
		if (cursorName != null) {
			cursorName.setParent(this);
		}
		this.cursorName = cursorName;
	}

	@Override
	protected void accept0(SQLASTVisitor visitor) {
		if (visitor.visit(this)) {
			acceptChild(visitor, cursorName);
		}
	    visitor.endVisit(this);
		
	}

	@Override
	public List<SQLObject> getChildren() {
		return Collections.<SQLObject>emptyList();
	}
}
