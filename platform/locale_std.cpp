#include "platform/locale.hpp"
#include "platform/preferred_languages.hpp"

#include <locale>

namespace platform
{
Locale StdLocale2Locale(std::locale const & languageLocale,
                        std::locale const & numericLocale,
                        std::locale const & currencyLocale)
{
  // Extract language code from language locale's name (format xx_yy.UTF-8).
  std::string languageCode;
  if (languageLocale.name().find('_') == 2)
    languageCode = languageLocale.name().substr(0, 2);

  // Extract country code from numeric locale's name (format xx_yy.UTF-8).
  std::string countryCode;
  if ((numericLocale.name().find('_') == 2) &&
      (numericLocale.name().find('.') == 5))
    countryCode = numericLocale.name().substr(3, 2);

  // Get currency code from currency locale.
  std::string currencyCode =
    std::use_facet<std::moneypunct<char, true>>(currencyLocale).curr_symbol();

  // Get decimal separator from numeric locale.
  std::string decimalSeparator = std::string(1,
    std::use_facet<std::numpunct<char>>(numericLocale).decimal_point());

  // Get grouping (thousands) separator from numeric locale.
  std::string groupingSeparator = std::string(1,
    std::use_facet<std::numpunct<char>>(numericLocale).thousands_sep());

  LOG(LDEBUG, ("Locale language: ", languageCode));
  LOG(LDEBUG, ("Locale country:  ", countryCode));
  LOG(LDEBUG, ("Locale currency: ", currencyCode));
  LOG(LDEBUG, ("Locale dec sep:  ", decimalSeparator));
  LOG(LDEBUG, ("Locale group sep:", groupingSeparator));

  return {languageCode, countryCode, currencyCode, decimalSeparator,
          groupingSeparator};
}

Locale GetCurrentLocale()
{
  // Get language locale from LANG environment variable.
  std::locale languageLocale(std::getenv("LANG"));
  LOG(LDEBUG, ("Language locale name: ", languageLocale.name()));

  // Get numeric locale from LC_NUMERIC environment variable.
  std::locale numericLocale(std::getenv("LC_NUMERIC"));
  LOG(LDEBUG, ("Numeric locale name:  ", numericLocale.name()));

  // Get currency locale from LC_MONETARY environment variable.
  std::locale currencyLocale(std::getenv("LC_MONETARY"));
  LOG(LDEBUG, ("Currency locale name: ", currencyLocale.name()));

  return StdLocale2Locale(languageLocale, numericLocale, currencyLocale);
}

bool GetLocale(std::string const localeName, Locale& result)
{
  try
  {
    std::locale loc(localeName);

    result = StdLocale2Locale(loc, loc, loc);

    return true;
  }
  catch(...)
  {
    return false;
  }
}
}  // namespace platform

