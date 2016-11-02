//
//  JMAutolayoutUtilities.h
//  JumioCore
//
//  Created by Cosmin-Valentin Popescu on 21/04/15.
//  Copyright (c) 2015 Jumio Inc. All rights reserved.
//

@interface JMAutolayoutUtilities : NSObject

+ (UIView*)autolayoutView;
+ (UILabel*)autolayoutLabel;
+ (UILabel*)autolayoutLabelWithJustifiedParagraphStyleText: (NSString *) text;
+ (UIImageView*)autolayoutImageView;
+ (UIActivityIndicatorView*)autolayoutActivityIndicatorView;
+ (UIButton*)autolayoutButton;
+ (UITableView *)autolayoutTableViewWithTableViewStyle:(UITableViewStyle) tableViewStyle;

@end
