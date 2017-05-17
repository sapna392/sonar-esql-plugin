package com.exxeta.iss.sonar.esql.tree;

import com.exxeta.iss.sonar.esql.api.tree.Tree;
import com.exxeta.iss.sonar.esql.api.tree.Tree.Kind;
import com.exxeta.iss.sonar.esql.api.tree.expression.CallExpressionTree;
import com.exxeta.iss.sonar.esql.api.tree.expression.ExpressionTree;
import com.exxeta.iss.sonar.esql.api.tree.expression.IdentifierTree;
import com.exxeta.iss.sonar.esql.api.tree.expression.ParenthesisedExpressionTree;
import com.exxeta.iss.sonar.esql.api.tree.lexical.SyntaxToken;
import com.exxeta.iss.sonar.esql.tree.impl.EsqlTree;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public final class SyntacticEquivalence {

  private SyntacticEquivalence() {
  }

  /**
   * @return true, if nodes are syntactically equivalent
   */
  public static boolean areEquivalent(List<? extends Tree> leftList, List<? extends Tree> rightList) {
    if (leftList.size() != rightList.size() || leftList.isEmpty()) {
      return false;
    }

    for (int i = 0; i < leftList.size(); i++) {
      Tree left = leftList.get(i);
      Tree right = rightList.get(i);
      if (!areEquivalent(left, right)) {
        return false;
      }
    }
    return true;
  }

  /**
   * @return true, if nodes are syntactically equivalent
   */
  public static boolean areEquivalent(@Nullable Tree leftNode, @Nullable Tree rightNode) {
    return areEquivalent((EsqlTree) leftNode, (EsqlTree) rightNode);
  }

  private static boolean areEquivalent(@Nullable EsqlTree leftNode, @Nullable EsqlTree rightNode) {
    if (leftNode == rightNode) {
      return true;
    }
    if (leftNode == null || rightNode == null) {
      return false;
    }
    if (leftNode.getKind() != rightNode.getKind()) {
      return false;
    } else if (leftNode.isLeaf()) {
      return areLeafsEquivalent(leftNode, rightNode);
    }

    Iterator<Tree> iteratorA = leftNode.childrenIterator();
    Iterator<Tree> iteratorB = rightNode.childrenIterator();

    while (iteratorA.hasNext() && iteratorB.hasNext()) {
      if (!areEquivalent(iteratorA.next(), iteratorB.next())) {
        return false;
      }
    }

    return !iteratorA.hasNext() && !iteratorB.hasNext();
  }

  /**
   * Caller must guarantee that nodes of the same kind.
   */
  @VisibleForTesting
  protected static boolean areLeafsEquivalent(EsqlTree leftNode, EsqlTree rightNode) {
    if (leftNode instanceof IdentifierTree) {
      return Objects.equal(((IdentifierTree) leftNode).name(), ((IdentifierTree) rightNode).name());
    } else if (leftNode instanceof SyntaxToken) {
      return Objects.equal(((SyntaxToken) leftNode).text(), ((SyntaxToken) rightNode).text());
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  
  public static ExpressionTree skipParentheses(ExpressionTree tree) {
	    if (tree.is(Tree.Kind.PARENTHESISED_EXPRESSION)) {
	      return skipParentheses(((ParenthesisedExpressionTree) tree).expression());
	    }else if (tree.is(Kind.CALL_EXPRESSION)){
	    	CallExpressionTree call = (CallExpressionTree)tree;
	    	List<Tree> children = new ArrayList<>();
	    	Iterator<Tree> it = call.childrenIterator();
	    	while (it.hasNext()){
	    		Tree child = it.next();
	    		if (child!=null){
	    			children.add(child);
	    		}
	    	}
	    	if (children.size()==1 && children.get(0)instanceof ExpressionTree){
	    		return skipParentheses((ExpressionTree)children.get(0));
	    	}
	    	return tree;
	    	
	    } else {
	    	return tree;
	    }
	  }

}
