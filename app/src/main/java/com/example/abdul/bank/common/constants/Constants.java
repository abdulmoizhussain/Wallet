package com.example.abdul.bank.common.constants;

import com.example.abdul.bank.R;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String WALLET_ID = "WALLET_ID";
    public static final String WALLET_IS_CLONE = "WALLET_IS_CLONE";

    public static class SearchTypes {
        public static final String ExactMatch = "searchType_ExactMatch";
        public static final String MatchAmount = "searchType_MatchAmount";
        public static final String KeywordsIncludeAll = "searchType_KeywordsIncludingAll";
        public static final String KeywordsAny = "searchType_KeywordsAny";
        public static final String SearchAmountAndKeywordsAny = "searchType_SearchAmountAndKeywordsAny";

        private static final Map<Integer, String> mapping = new HashMap<>();

        static {
            mapping.put(R.id.radioBtnExactMatch, ExactMatch);
            mapping.put(R.id.radioBtnMatchAmount, MatchAmount);
            mapping.put(R.id.radioBtnKeywordsAny, KeywordsAny);
            mapping.put(R.id.radioBtnKeywordsIncludeAll, KeywordsIncludeAll);
            mapping.put(R.id.radioBtnSearchAmountAndKeywordsAny, SearchAmountAndKeywordsAny);
        }

        public static Integer getRId(String constantValue) {
            if (constantValue == null) return null;

            for (Map.Entry<Integer, String> entry : mapping.entrySet()) {
                if (entry.getValue().equals(constantValue)) {
                    return entry.getKey();
                }
            }
            return null;
        }

        public static String getConstant(Integer rId) {
            if (mapping.containsKey(rId)) {
                return mapping.get(rId);
            }
            return null;
        }
    }
}
